package oj.judge.runner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import oj.judge.common.Callback;
import oj.judge.common.Conf;
import oj.judge.common.Result;
import oj.judge.common.Solution;

public class Runner extends Thread {
	private static final String label = "Runner::";

	public enum E { ERROR, FINISH };
	public Map<E, Callback> listener;

	public void reg(E e, Callback c) {
		listener.put(e, c);
	}
	public void emit(E e, Object o) {
		listener.get(e).o = o;
		listener.get(e).call();
	}

	@Override
	public void run() {
		result = solution.result.get(0);
		compile();
		if (solution.result.get(0).verdict == Result.Verdict.NONE) {
			for (int i = 0; i < solution.problem.totalCase; i++) {
				result = solution.result.get(i);

				judge(i);
				if (solution.result.get(0).verdict != Result.Verdict.AC)
					break;
			}
		}
		cleanUp(runningPath);
		emit(E.FINISH, solution);
	}

	public Integer id;
	public Solution solution;
	public Result result; // for easy access

	public Path runningPath;
	public Path executable;
	public Path securityPolicy;

	public long timeOut;
	public int bufferSize;

	public Runner(Integer id, Path runningPath, Solution solution) {
		this.id = id;
		this.listener = new HashMap<E, Callback>();

		this.timeOut = Conf.timeOut();
		this.bufferSize = Conf.bufferSize();
		
		this.runningPath = runningPath;
		this.executable = Paths.get(runningPath + "/" + solution.id + "/1");
		this.securityPolicy = Conf.securityPolicyFile();

		this.solution    = solution;
	}

	public void judge(int caseNo) {
		Path input = Paths.get(runningPath + "/" + solution.id + "/" + caseNo + "/input");
		Path output = Paths.get(runningPath + "/" + solution.id + "/" + caseNo + "/output");
		Path error = Paths.get(runningPath + "/" + solution.id + "/" + caseNo + "/error");
		Path metrics = Paths.get(runningPath + "/" + solution.id + "/" + caseNo + "/metrics");

//EXE=$1
//INFILE=$2
//OUTFILE=$3
//ERRORFILE=$4
//TIMEOUT=$5

		if (!solution.problem.saveInput(caseNo, input)) {
			result.verdict = Result.Verdict.JE;
			return ;
		}

		try {
			output.toFile().createNewFile();
			error.toFile().createNewFile();
			metrics.toFile().createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			result.verdict = Result.Verdict.JE;
			return ;
		}

		ProcessBuilder pb = getProcessBuilder(solution.language, input, output, error, metrics);

		try {
			Process p = pb.start();

			Thread.sleep((long) (solution.problem.timeLimit + timeOut));
			if (p.isAlive()) {
				p.destroyForcibly();
				result.verdict = Result.Verdict.TL;
				return ;
			}

			if (error.toFile().length() > bufferSize || output.toFile().length() > bufferSize) {
				result.verdict = Result.Verdict.OL;
				return ;
			}
			else {
				result.output = Files.lines(output).reduce("", (a, b) -> a + '\n' + b);
				result.error = Files.lines(error).reduce("", (a, b) -> a + '\n' + b);
				result.metrics = Files.lines(metrics).reduce("", (a, b) -> a + '\n' + b);
			}

			Checker.check(result, solution.problem.timeLimit, solution.problem.memoryLimit, solution.problem.output.get(caseNo));
		} catch (IOException e) {
			e.printStackTrace();
			result.verdict = Result.Verdict.JE;
		} catch (InterruptedException e) {
			e.printStackTrace();
			result.verdict = Result.Verdict.JE;
		}
	}

	public void compile() {
		Path source = Paths.get(runningPath + "/" + solution.id + "/source");
		executable = executable;
		Path compileOut = Paths.get(runningPath + "/" + solution.id + "/compileOut");
		Path compileError = Paths.get(runningPath + "/" + solution.id + "/compileError");

//SOURCE=$1
//OUT=$2
//COMOUT=$3
//COMERROR=$4

		if (!solution.saveSource(source)) {
			result.verdict = Result.Verdict.JE;
			return ;
		}

		try {
			boolean ok = Compiler.compile(solution.language, source, executable, compileOut, compileError);
			if (!ok)
				result.verdict = Result.Verdict.CE;
		} catch (IOException e) {
			e.printStackTrace();
			result.verdict = Result.Verdict.JE;
		}
	}

	public ProcessBuilder getProcessBuilder(Solution.Language language, Path input, Path output, Path error, Path metrics) {
		String scriptPath = Conf.runScript();
		switch (language) {
			case CPP:
				scriptPath = scriptPath + "/CPP.sh";
				break;
			case JAVA:
				scriptPath = scriptPath + "/JAVA.sh";
				break;
			default:
				return null;
		}

		return new ProcessBuilder(
				scriptPath,
				executable.toString(),
				input.toString(),
				output.toString(),
				error.toString(),
				metrics.toString()
		);
	}

	public void cleanUp(Path path) {
		for (File f : path.toFile().listFiles())
			delete(f);
	}
	public void delete(File f) {
		if (f.isDirectory())
			for (File ff : f.listFiles())
				delete(f);
		f.delete();
	}
}
