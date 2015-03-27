package oj.judge.remote;

import java.util.Map;

import oj.judge.common.Callback;

public class Remote extends Thread {
	public enum E { NEWPROBLEM, ERROR };
	public Map<E, Callback> listener;
	
	public void reg(E e, Callback c) {
		listener.put(e, c);
	}
	public void emit(E e) {
		listener.get(e).call();
	}
	
	public int interval = 10000;
	
	public Remote() {
		
	}
	
	public void getProblem() {
		
		emit(E.NEWPROBLEM);
	}
	
	public void terminate() {
		this.interrupt();
	}
	
	@Override
	public void run() {
		while (!this.interrupted()) {
			getProblem();
			
			try {
				this.sleep(interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
