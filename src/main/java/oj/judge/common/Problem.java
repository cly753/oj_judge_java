package oj.judge.common;

public class Problem {
    public Long id;
    
    public int timeLimit; // in ms. 0 for not specified.
    public int memoryLimit; // in MB. 0 for not specified.
    public boolean specialJudge;

    public String resourcesHash;

    public String input  = "This is input from judge.";
    public String output = "This is output from judge.";
    
    public Problem() {
		//
		// TODO
		// 
    }
}
