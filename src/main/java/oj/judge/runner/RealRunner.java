package oj.judge.runner;

import oj.judge.common.Callback;
import oj.judge.common.Solution;

import java.util.Map;

/**
 * Created by t_chenli on 3/27/2015.
 */
class RealRunner extends Thread {
    public enum EVENT { JUDGE_FINISH };
    public Map<EVENT, Callback> listener;

    RealRunner(Solution solution) {

    }

    @Override
    public void run() {

    }

	public void event(EVENT e) {
		listener.get(e).doAction();
	}
	public void event(EVENT e, Callback li) {
		listener.put(e, li);
	}
}