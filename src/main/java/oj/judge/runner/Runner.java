package oj.judge.runner;

import oj.judge.center.IRunner;
import oj.judge.common.Solution;

public class Runner implements IRunner {
	public Solution solution;
	public Integer id;
	
	public Runner(Integer id) {
		this.id = id;
	}
	
	public boolean judge(Solution solution) {
		//
		// TODO
		// 
		
		return true; // filled up result in solution : Solution
	}
	
	public boolean link() {
		//
		// TODO
		//
		return false;
	}
	
	//
	// TODO
	// 
}
