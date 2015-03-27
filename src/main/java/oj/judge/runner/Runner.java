package oj.judge.runner;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oj.judge.center.IChecker;
import oj.judge.center.IRunner;
import oj.judge.common.Callback;
import oj.judge.common.Solution;

public class Runner implements IRunner, Runnable {
	private static final String label = "Runner::";
	
	public enum E { ERROR, FINISH };
	public Map<E, Callback> listener;
	
	public void reg(E e, Callback c) {
		listener.put(e, c);
	}
	public void emit(E e) {
		listener.get(e).call();
	}
	
	@Override
	public void run() {
		generate();
		prepareInput();
		execute();
		cleanUp();
		checker.check(this.solution);

		emit(E.FINISH);
	}

	@Override
	public void judge(Path runningPath, Solution solution, IChecker checker, Callback callback) {
		this.runningPath = runningPath;
		this.inputFile   = Paths.get(runningPath + "/" + "in");
		this.outputFile  = Paths.get(runningPath + "/" + "out");
		this.errorFile   = Paths.get(runningPath + "/" + "error");
		
		this.solution    = solution;
		this.checker     = checker;

		listener.put(E.FINISH, callback);
				
		me = new Thread(this);
		me.start();
	}

	public Integer id;
	public Solution solution;
	
	public Path inputFile;
	public Path outputFile;
	public Path errorFile;
	public Path runningPath;
	
	public static int timeOut = 500;
	public static int outputBufferSize = 10000000;

	public IChecker checker;

	public Thread me;

	public Runner(Integer id) {
		this.id = id;
		this.listener = new HashMap<E, Callback>();
	}
	
	public boolean generate() {
		if (solution.judged)
			return true;
		
		try {
			boolean result = solution.compileSave(runningPath);
			if (!result) {
				solution.result = Solution.Result.CE;
				solution.judged = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public boolean prepareInput() {
		try {
			OpenOption[] options = new OpenOption[] { WRITE, CREATE, TRUNCATE_EXISTING };
			BufferedWriter writer = Files.newBufferedWriter(inputFile, Charset.forName("US-ASCII"), options);
		    writer.write(solution.problem.input, 0, solution.problem.input.length());
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean cleanUp() {
		boolean ok = true;
		ok &= delete(inputFile.toFile());
		ok &= delete(outputFile.toFile());
		ok &= delete(errorFile.toFile());
		ok &= delete(Paths.get(runningPath + "/" + solution.classToRun + ".class").toFile());
//		ok &= delete(Paths.get(runningPath + "/" + "SecureRunner").toFile());
		return ok;
	}
	public boolean delete(File f) {
		if(f.exists() && !f.isDirectory())
			return f.delete();
		return false;
	}
	public boolean execute() {
		if (solution.judged)
			return true;
		
		List<String> cmd = Arrays.asList("java", runningPath + "/" + "SecureRunner", "0", ".", ".");
		ProcessBuilder pb = new ProcessBuilder(cmd);
		
		pb.redirectInput(inputFile.toFile());
		
		try {
			Process p = pb.start();
			
			BufferedReader output = new BufferedReader (new InputStreamReader(p.getInputStream()), outputBufferSize);
			BufferedReader error = new BufferedReader (new InputStreamReader(p.getErrorStream()), outputBufferSize);
			error.close();
			
			System.out.println(label + "sleep");
			Thread.sleep(timeOut);
			if (p.isAlive()) {
				p.destroyForcibly();
				System.out.println(label + "force kill");
			}
			System.out.println(label + "wakeup");
			
			char[] buffer = new char[outputBufferSize];
			int actualRead = output.read(buffer, 0, outputBufferSize);
			output.close();

		    if (actualRead == -1) {
		    	solution.output = "";
		    }
		    else if (actualRead == outputBufferSize) {
				solution.result = Solution.Result.OL;
				solution.judged = true;
			}
			else {
				solution.output = new String(buffer, 0, actualRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return false;
	}
}
