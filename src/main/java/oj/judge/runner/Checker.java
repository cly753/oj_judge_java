package oj.judge.runner;

import oj.judge.common.Solution;

/**
 * Created by t_chenli on 3/27/2015.
 */
public class Checker {
    public void check(Solution solution) {
    	if (solution.judged())
    		return ;
    	
        if (solution.runnerResult.has("ERROR")) {
        	solution.result = Solution.Result.RE;
        	return ;
        }
        
        solution.timeUsed = solution.runnerResult.getLong("TIME") / 1000000000.0;
        if (solution.timeUsed > solution.problem.timeLimit) {
        	solution.result = Solution.Result.TL;
        	return ;
        }
        
        if (cmp(solution.output, solution.problem.output))
        	solution.result = Solution.Result.AC;
        else
        	solution.result = Solution.Result.WA;
    }
    
    private boolean cmp(String p, String s) {
    	String[] pl = p.split("\n");
    	String[] sl = p.split("\n");
    	
    	if (pl.length != sl.length)
    		return false;
    	
    	for (int i = 0; i < pl.length; i++)
    		if (!pl[i].trim().equals(sl[i].trim()))
    			return false;
    	return true;
    }
}
