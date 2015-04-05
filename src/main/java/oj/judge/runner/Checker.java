package oj.judge.runner;

import oj.judge.common.Conf;
import oj.judge.common.Result;

public class Checker {
	public static final String label = "Checker::";
	
	public static void check(Result result, double timeLimit, double memoryLimit, String correct) {

		if (Conf.debug()) System.out.println("time limit: " + timeLimit + ", memory limit: " + memoryLimit);
		if (Conf.debug()) System.out.println("output  : ----\n" + result.output  + "\n------------");
		if (Conf.debug()) System.out.println("correct : ----\n" + correct        + "\n------------");
		if (Conf.debug()) System.out.println("error   : ----\n" + result.error   + "\n------------");
		if (Conf.debug()) System.out.println("metrics : ----\n" + result.metrics + "\n------------");

		if (result.error != null && result.error.length() > 0) {
			if (result.error.contains("OutOfMemory"))
				result.verdict = Result.Verdict.ML;
			else
				result.verdict = Result.Verdict.RE;
			return ;
		}

		parseMetrics(result);

		if (Conf.debug()) System.out.println(label + "time   used : " + result.timeUsed   + " ms ( " + timeLimit   + " ms )");
		if (Conf.debug()) System.out.println(label + "memory used : " + result.memoryUsed + " KBytes ( " + memoryLimit + " KBytes )");

		if (result.memoryUsed > memoryLimit) {
			result.verdict = Result.Verdict.ML;
			return ;
		}

		if (result.timeUsed > timeLimit) {
			result.verdict = Result.Verdict.TL;
			return ;
		}

		if (check(result.output, correct))
			result.verdict = Result.Verdict.AC;
		else
			result.verdict = Result.Verdict.WA;
	}

	public static boolean check(String p, String s) {
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

	public static void parseMetrics(Result result) {
		result.timeUsed = 0;
		result.memoryUsed = 0;

		String[] metrics = result.metrics.split("\n");
		if (metrics.length < 2)
			return ;

		try {
			result.timeUsed = (int)(1000 * Double.parseDouble(metrics[0]));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		try {
			result.memoryUsed = (int)(Double.parseDouble(metrics[1]));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
