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

        IRunner ir = new Runner(0);
        ir.judge(Paths.get(path), new Solution(new Problem()), new Checker(), new Callback() {
            @Override
            public void call() {
                System.out.println(label + "Callback hello world");
            }
        });
        System.out.println("Waiting for result...");
	}
}
