package oj.judge.center;

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import oj.judge.common.Callback;
import oj.judge.common.Conf;
import oj.judge.common.Formatter;
import oj.judge.common.Problem;
import oj.judge.common.Solution;
import oj.judge.remote.Remote;
import oj.judge.runner.Checker;
import oj.judge.runner.Runner;

public class Center extends Thread {
    private static final String label = "Center::";
    
    private Remote remote;
    private ConcurrentHashMap<Integer, Runner> runner;
    
    public static void main(String[] args) {
    	Center c = new Center();
    	c.start();
    }
    
    public Center() {
    	runner = new ConcurrentHashMap<Integer, Runner>();
    }

	@Override
	public void run() {
    	if (!Conf.init())
    		return ;
    	
		remote = new Remote(Conf.fetchInterval());
		remote.setName("Remote");
		remote.reg(Remote.E.NEWPROBLEM, new Callback() {
            @Override
            public void call() {
            	if (Conf.debug()) System.out.println(label + "Callback Remote.E.NEWPROBLEM");
            	
                Path runningPath = Conf.runningPath();
                
                final Integer id = runner.size();
                Runner r = new Runner(id, runningPath, (Solution)o);
                r.setName("Runner-" + id);
                runner.put(id, r);
                r.reg(Runner.E.FINISH, new Callback() {
                    @Override
                    public void call() {
                    	if (Conf.debug()) System.out.println(label + "Callback Runner.E.FINISH");
                    	if (Conf.debug()) System.out.println(label + (Solution)o);
                    	
                    	remote.pushSolution((Solution)o);
                    	runner.remove(id);
                    }
                });
                
                r.start();
                if (Conf.debug()) System.out.println("Waiting for result...");
            }
		});
		remote.start();
	}
}
