package oj.judge.common;

import java.util.Date;

public class Solution {
	public Long id;
	public Long problemId;
	public Problem problem;
	
    // Used by judge
    public int language;
    public String code;

    // Used by judge
    public Date receiveTime;
    public Date judgeTime;

    public int result;
    public String judgeResponse;
    public int timeUsed;
    public int memoryUsed;

    public boolean judged;
    
    public Solution() {
		//
		// TODO
		// 
    }
    
    public Class<?> getCompiled() {
    	// 
    	// TODO
    	// 
    	return null;
    }
    
    public boolean saveCompiledTo(String path) {
    	// 
    	// TODO
    	//
    	return false;
    }
    
	//
	// TODO
	// 
}
