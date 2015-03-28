package oj.judge.center;

import java.nio.file.Path;

import oj.judge.common.Callback;
import oj.judge.common.Conf;
import oj.judge.common.Problem;
import oj.judge.common.Solution;
import oj.judge.remote.Remote;
import oj.judge.runner.Checker;
import oj.judge.runner.Runner;

public class Center extends Thread {
    private static final String label = "Center::";
    
    public static void main(String[] args) {
    	Conf.init();
    	
    	Center c = new Center();
    	c.start();
    }

	@Override
	public void run() {
		Remote remote = new Remote(2000);
		remote.reg(Remote.E.NEWPROBLEM, new Callback() {
            @Override
            public void call() {
            	System.out.println(label + "Callback Remote.E.NEWPROBLEM");
            	
                Path runningPath = Conf.runningPath();
                
                Runner runner = new Runner(0, runningPath, new Solution(new Problem()), new Checker());
                runner.reg(Runner.E.FINISH, new Callback() {
                    @Override
                    public void call() {
                        System.out.println(label + "Callback Runner.E.FINISH");
                    }
                });
                
                runner.start();
                System.out.println("Waiting for result...");
            }
		});
		remote.start();
	}
}
