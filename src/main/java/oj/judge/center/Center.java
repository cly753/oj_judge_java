package oj.judge.center;

import java.nio.file.Paths;

import oj.judge.common.Callback;
import oj.judge.common.Problem;
import oj.judge.common.Solution;
import oj.judge.runner.Checker;
import oj.judge.runner.Runner;

public class Center extends Thread {
    private static final String label = "Center::";
    
    public static void main(String[] args) {
    	Center c = new Center();
    	c.start();
    }

	@Override
	public void run() {
        String path = "C:/Users/" + System.getProperty("user.name") + "/Desktop"; // for testing

        Runner runner = new Runner(0, Paths.get(path), new Solution(new Problem()), new Checker());
        runner.reg(Runner.E.FINISH, new Callback() {
            @Override
            public void call() {
                System.out.println(label + "Callback hello world");
            }
        });
        
        runner.start();
        System.out.println("Waiting for result...");
	}
}
