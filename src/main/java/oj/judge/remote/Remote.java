package oj.judge.remote;

import java.util.HashMap;
import java.util.Map;

import oj.judge.common.Callback;
import oj.judge.common.Problem;
import oj.judge.common.Solution;
import oj.judge.runner.Runner.E;

public class Remote extends Thread {
	public enum E { NEWPROBLEM, ERROR };
	public Map<E, Callback> listener;
	
	public void reg(E e, Callback c) {
		listener.put(e, c);
	}
	public void emit(E e) {
		listener.get(e).call();
	}
	
	public long interval = 10000L;
	
	public Remote(long interval) {
		this.listener = new HashMap<E, Callback>();
		this.interval = interval;
	}
	
	public Solution getSolution() {
		
		return new Solution(new Problem());
	}
	
	public void pushResult() {
		
	}
	
	public void terminate() {
		this.interrupt();
	}
	
	@Override
	public void run() {
		emit(E.NEWPROBLEM);
		
//		while (!Thread.interrupted()) {
//			Solution solution = getSolution();
//			if (solution != null)
//				emit(E.NEWPROBLEM);
//			
//			try {
//				Thread.sleep(interval);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
}