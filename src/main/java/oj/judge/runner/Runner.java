package oj.judge.runner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import oj.judge.center.IRunner;
import oj.judge.common.Listener;
import oj.judge.common.Solution;

public class Runner implements IRunner {
	public Solution solution;
	public Integer id;
	
	public String runningPath;
	
	public Map<String, List<Listener>> listeners;
	
	public Runner(Integer id) {
		this.id = id;
	}
	
	public boolean judge(Solution solution) {
		generate();
		
		
		return true; // filled up result in solution : Solution
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
	
	public boolean judge() {
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
	
	// TODO
	// http://www.giocc.com/writing-an-event-driven-framework-with-java.html
//	public void event(String e) {
//		List<Listener> l = listeners.get(e);
//		if (l != null) for (Listener li : l)
//			li.doAction();
//	}
//	public void event(String e, Listener li) {
//		List<Listener> l = listeners.get(e);
//		if (l == null) l = new ArrayList<Listener>();
//		l.add(li);
//	}
	
	//
	// TODO
	// 
}
