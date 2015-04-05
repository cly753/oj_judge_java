package oj.judge.runner;

import java.io.File;
import java.io.IOException;
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

	public enum E { FINISH, ERROR };
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

		if (solution.problem.totalCase == 0) {
			if (Conf.debug()) System.out.println(label + "solution.problem.totalCase == 0");
			return ;
		}

		result = new Result();
		compile();
		if (result.verdict == Result.Verdict.NONE) {
			for (int i = 0; i < solution.problem.totalCase; i++) {
				solution.result.add(result);

				judge(i);
				if (solution.result.get(0).verdict != Result.Verdict.AC)
					break;

				result = new Result();
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
	public int outputLimit;

	public Runner(Integer i, Path savePath, Solution sol) {
		id = i;
		listener = new HashMap<>();

		solution = sol;

		timeOut = Conf.timeOut();
		outputLimit = Conf.outputLimit();

		runningPath = Paths.get(savePath + "/" + sol.id);
		executable = Paths.get(runningPath + "/" + Conf.compileName());

		securityPolicy = Conf.securityPolicyFile();

		if (!Files.exists(runningPath))
			runningPath.toFile().mkdirs();
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

		result.verdict = new Executor(
				solution.language,
				solution.problem.timeLimit,
				solution.problem.memoryLimit,
				executable,
				input,
				output,
				error,
				metrics,
				metrics
		).execute();

		if (result.verdict != Result.Verdict.NONE) {
			return ;
		}

		if (Conf.debug()) System.out.println(label + "output size = " + output.toFile().length());
		if (Conf.debug()) System.out.println(label + "error size = " + error.toFile().length());
		if (output.toFile().length() >= outputLimit || error.toFile().length() >= outputLimit) {
			result.verdict = Result.Verdict.OL;
			return ;
		}

		Charset charset;
		if (Conf.debug()) System.out.println(System.getProperty("os.name"));
		if (System.getProperty("os.name").contains("Windows")) {
			charset = Charset.forName("utf-16"); // "windows-1252"
		}
		else {
			charset = Charset.defaultCharset();
		}

		try {
			result.output = new String(Files.readAllBytes(output), charset);
			result.error = new String(Files.readAllBytes(error), charset);
			result.metrics = new String(Files.readAllBytes(metrics), charset);
		} catch (IOException e) {
			e.printStackTrace();
			result.verdict = Result.Verdict.JE;
			return ;
		}

		Checker.check(result, solution.problem.timeLimit, solution.problem.memoryLimit, solution.problem.output.get(caseNo));
	}

	public void compile() {
		Path source = Paths.get(runningPath + "/source");
//		executable = executable;
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
