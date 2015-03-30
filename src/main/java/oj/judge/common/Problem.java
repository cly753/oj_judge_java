package oj.judge.common;

public class Problem {
    public Long id;
    
    public double timeLimit = 1; // in ms. 0 for not specified.
    public double memoryLimit = 200; // in MB. 0 for not specified.
    public boolean specialJudge = false;

    public String resourcesHash = "";

    public String input  = "This is input from judge.";
    public String output = "This is output from judge.";
    
    public Problem() {
		//
		// TODO
		// 
    }
}
