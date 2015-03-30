package oj.judge.runner;

import oj.judge.common.Conf;
import oj.judge.common.Solution;

/**
 * Created by t_chenli on 3/27/2015.
 */
public class Checker {
	public static final String label = "Checker::";
	public void check(Solution solution) {
		if (solution.judged())
			return ;

		if (solution.error != null && solution.error.length() > 0) {
			if (solution.error.contains("OutOfMemory"))
				solution.result = Solution.Result.ML;
			else
				solution.result = Solution.Result.JE;
			return ;
		}

		if (solution.runnerResult.has("ERROR")) {
			String error = solution.runnerResult.getString("ERROR");

			if (Conf.debug()) System.out.println(label + "runtime error: " + error);

			solution.result = Solution.Result.RE;
			return ;
		}

		solution.timeUsed = solution.runnerResult.getLong("TIME") / 1000000000.0;

		if (Conf.debug()) System.out.println(label + "time used : " + solution.timeUsed);
		if (Conf.debug()) System.out.println(label + "time limit: " + solution.problem.timeLimit);

		if (solution.timeUsed > solution.problem.timeLimit) {
			solution.result = Solution.Result.TL;
			return ;
		}

		if (Conf.debug()) System.out.println(label + "solution output: " + solution.output);
		if (Conf.debug()) System.out.println(label + "standard output: " + solution.problem.output);

		if (check(solution.output, solution.problem.output))
			solution.result = Solution.Result.AC;
		else
			solution.result = Solution.Result.WA;
	}

	public boolean check(String p, String s) {
		String[] pl = p.split("\n");
		String[] sl = s.split("\n");

		if (pl.length != sl.length)
			return false;

		for (int i = 0; i < pl.length; i++) {
			String pli = pl[i].trim();
			String sli = sl[i].trim();

			if (!pli.equals(sli))
				return false;
		}
		return true;
	}
}
