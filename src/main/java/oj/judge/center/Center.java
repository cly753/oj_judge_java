package oj.judge.center;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

import oj.judge.common.Callback;
import oj.judge.common.Conf;
import oj.judge.common.Problem;
import oj.judge.common.Solution;
import oj.judge.remote.Remote;
import oj.judge.runner.Runner;
import org.json.JSONObject;

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
        Runner r = new Runner(id, savePath, Solution.getFakeSolution());

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
