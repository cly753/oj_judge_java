package oj.judge.center;

import java.nio.file.Path;

import oj.judge.common.Callback;
import oj.judge.common.Conf;
import oj.judge.common.Solution;
import oj.judge.runner.Runner;

public class Center extends Thread {
    private static final String label = "Center::";

    public static void main(String[] args) {
    	Center c = new Center();
    	c.start();
    }

	@Override
	public void run() {
    	if (!Conf.init())
    		return ;

//        Remote remote = new Remote(Conf.fetchInterval());
//        try {
//            byte[] raw = remote.getProblemResourcesZip(1L);
//
//            Problem problem = new Problem(1L, "", raw);
//            System.out.println(problem);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        System.out.println(solution);

        Integer id = 0;
    	Path savePath = Conf.runningPath();
        Runner r = new Runner(id, savePath, Solution.getMockSolution());

        r.setName("Runner-" + id);
        r.reg(Runner.E.FINISH, new Callback() {
            @Override
            public void call() {
            	if (Conf.debug()) System.out.println(label + "Callback Runner.E.FINISH");
            	if (Conf.debug()) System.out.println(label + o);
                if (Conf.debug()) System.out.println(label + ((Solution)o).getResultJson());
            }
        });

        r.start();
        if (Conf.debug()) System.out.println("Waiting for result...");
	}
}
