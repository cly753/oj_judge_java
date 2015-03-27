package oj.judge.runner;

import oj.judge.center.IChecker;
import oj.judge.common.Solution;

/**
 * Created by t_chenli on 3/27/2015.
 */
public class Checker implements IChecker {

    @Override
    public void check(Solution solution) {
        if (solution.output == null)
            return ;
        if (solution.problem.output == null)
        	return ;
        
        int result = solution.problem.output.compareTo(solution.output);
        if (result == 0)
            return ;
        return ;
    }
}
