package oj.judge.runner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import oj.judge.center.IChecker;
import oj.judge.center.IRunner;
import oj.judge.common.Callback;
import oj.judge.common.Solution;

public class Runner implements IRunner, Runnable {
	private static final String label = "Runner::";
	@Override
	public void run() {
		generate();
		execute();
		checker.checck(this.solution);
		callback.call();
	}

	@Override
	public void judge(Path runningPath, Solution solution, IChecker checker, Callback callback) {
		this.runningPath = runningPath;
		this.solution = solution;
		this.checker = checker;
		this.callback = callback;
		new Thread(this).start();
	}

	public Integer id;
	public Solution solution;
	public Path runningPath;
	public int timeOut = 500;

	public IChecker checker;
	public Callback callback;

	public Runner(Integer id) {
		this.id = id;
	}
	
	public boolean generate() {
		try {
			solution.compileSave(runningPath);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	public boolean execute() {
		exe();
		
		//
		// TODO
		//
		return false;
	}
	public boolean exe() {
//		List<String> cmd = Arrays.asList("java", runningPath + "/" + solution.classToRun);
		List<String> cmd = Arrays.asList("java", runningPath + "/" + "SecureRunner");
		ProcessBuilder pb = new ProcessBuilder(cmd);

		try {
			Process process = pb.start();
			System.out.println(label + "sleep");
			Thread.sleep(timeOut);
			if (process.isAlive()) {
				process.destroyForcibly();
				System.out.println(label + "force kill");

				//
				// TODO
				// http://docs.oracle.com/javase/7/docs/api/java/lang/ProcessBuilder.html
				// https://docs.oracle.com/javase/8/docs/api/java/lang/Process.html#destroyForcibly--
				//
			}
			System.out.println(label + "wakeup");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return false;
	}

	
	//
	// TODO
	// 
}
