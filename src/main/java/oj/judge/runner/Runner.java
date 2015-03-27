package oj.judge.runner;

import java.io.IOException;
import java.nio.file.Path;

import oj.judge.center.IChecker;
import oj.judge.center.IRunner;
import oj.judge.common.Callback;
import oj.judge.common.Solution;

public class Runner implements IRunner, Runnable {
	@Override
	public void run() {
		generate();
		execute();
		checker.checck(this.solution);
		callback.doAction();
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
//		final long timeLimit = 0L;
//		final Runner parent = this;
//		new Thread() {
//			@Override
//			public void run() {
//				Thread.sleep(timeLimit);
//				parent.interrupt();
//			}
//		}
		
		//
		// TODO
		//
		return false;
	}

	
	//
	// TODO
	// 
}
