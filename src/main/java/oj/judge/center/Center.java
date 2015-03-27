package oj.judge.center;

import oj.judge.common.Callback;
import oj.judge.common.Solution;
import oj.judge.runner.Checker;
import oj.judge.runner.Runner;

import java.nio.file.Paths;

public class Center {
    private static final String label = "Center::";

    public static void main(String[] args) {
        String path = "C:\\Users\\" + System.getProperty("user.name") + "\\Desktop"; // for testing

        IRunner ir = new Runner(0);
        ir.judge(Paths.get(path), new Solution(), new Checker(), new Callback() {
            @Override
            public void doAction() {
                System.out.println(label + "Callback hello world");
            }
        });
        System.out.println("Waiting for result...");
    }

	//
	// TODO
	// 
}
