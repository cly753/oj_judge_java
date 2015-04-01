package oj.judge.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
		if (runningPath.toFile().exists())
			cleanUp(runningPath);
		runningPath.toFile().mkdir();

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

		this.runningPath = Paths.get(runningPath + "/" + solution.id);
		this.executable = Paths.get(this.runningPath + "/" + Conf.compileName());
				
		this.securityPolicy = Conf.securityPolicyFile();

		this.solution    = solution;
	}

	public void judge(int caseNo) {
		Path casePath = Paths.get(runningPath + "/" + (1 + caseNo));
		if (casePath.toFile().exists())
			cleanUp(casePath);
		casePath.toFile().mkdir();

		Path input = Paths.get(   casePath + "/input"   );
		Path output = Paths.get(  casePath + "/output"  );
		Path error = Paths.get(   casePath + "/error"   );
		Path metrics = Paths.get( casePath + "/metrics" );

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
		
		if (Conf.debug()) System.out.println(label + "ProcessBuilder...");
		if (Conf.debug()) for (String s : pb.command()) System.out.print(s + " ");
		if (Conf.debug()) System.out.println();
		
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
				
				Charset charset;
				if (System.getProperty("os.name").contains("Windows")) {
					charset = Charset.forName("windows-1252");
					// http://stackoverflow.com/questions/1835430/byte-order-mark-screws-up-file-reading-in-java/7390288#7390288
				}
				else {
					charset = Charset.defaultCharset();
				}
				
				result.output = Files.lines(output, charset).reduce("", (a, b) -> a + '\n' + b);
				result.error = Files.lines(error, charset).reduce("", (a, b) -> a + '\n' + b);
				result.metrics = Files.lines(metrics, charset).reduce("", (a, b) -> a + '\n' + b);
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
		Path source = Paths.get(runningPath + "/source");
		executable = executable;
		Path compileOut = Paths.get(runningPath + "/compileOut");
		Path compileError = Paths.get(runningPath + "/compileError");

		//SOURCE=$1
		//OUT=$2
		//COMOUT=$3
		//COMERROR=$4

		if (!solution.saveSource(source)) {
			result.verdict = Result.Verdict.JE;
			return ;
		}

		try {
			boolean ok = Compiler.compile(
					solution.language,
					source.toAbsolutePath(),
					executable.toAbsolutePath(),
					compileOut.toAbsolutePath(),
					compileError.toAbsolutePath()
					);
			if (!ok)
				result.verdict = Result.Verdict.CE;
		} catch (IOException e) {
			e.printStackTrace();
			result.verdict = Result.Verdict.JE;
		}
	}

	public ProcessBuilder getProcessBuilder(Solution.Language language, Path input, Path output, Path error, Path metrics) {
		String scriptPath = Conf.runScript().toAbsolutePath().toString();
		String suffixBat;
		String suffixExe;
		if (Conf.debug()) System.out.println(System.getProperty("os.name"));

		if (System.getProperty("os.name").contains("Windows")) {
			suffixBat = ".bat";
			suffixExe = ".exe";
		}
		else {
			suffixBat = ".sh";
			suffixExe = "";
		}

		switch (language) {
		case CPP:
			scriptPath = scriptPath + "/CPP" + suffixBat;
			break;
		case JAVA:
			scriptPath = scriptPath + "/JAVA" + suffixBat;
			break;
		default:
			return null;
		}

		return new ProcessBuilder(
				scriptPath,
				executable.toAbsolutePath().toString() + suffixExe,
				input.toAbsolutePath().toString(),
				output.toAbsolutePath().toString(),
				error.toAbsolutePath().toString(),
				metrics.toAbsolutePath().toString()
				);
	}

	public void cleanUp(Path path) {
		for (File f : path.toFile().listFiles())
			delete(f);
	}
	public void delete(File f) {
		if (f.isDirectory())
			for (File ff : f.listFiles()) {
				if (Conf.debug()) System.out.println("deleting " + ff.getAbsolutePath() + "...");
//				delete(ff);
			}
		if (Conf.debug()) System.out.println("deleting " + f.getAbsolutePath() + "...");
//		f.delete();
	}
}
