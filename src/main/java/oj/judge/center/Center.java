package oj.judge.center;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

import oj.judge.common.Callback;
import oj.judge.common.Conf;
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
//        Solution solution = new Solution();
//        try {
//            remote.judgeFetchSolution(solution);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println(solution);


        Integer id = 0;
    	Path runningPath = Conf.runningPath();
        Runner r = new Runner(id, runningPath, Solution.getFakeSolution());

        r.setName("Runner-" + id);
        r.reg(Runner.E.FINISH, new Callback() {
            @Override
            public void call() {
            	if (Conf.debug()) System.out.println(label + "Callback Runner.E.FINISH");
            	if (Conf.debug()) System.out.println(label + (Solution)o);
            }
        });

        r.start();
        if (Conf.debug()) System.out.println("Waiting for result...");
	}
}
