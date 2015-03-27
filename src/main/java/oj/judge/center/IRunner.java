package oj.judge.center;

import oj.judge.common.Callback;
import oj.judge.common.Solution;
import oj.judge.runner.Runner;

import java.nio.file.Path;

public interface IRunner {
	public void judge(Path runningPath, Solution solution, IChecker checker, Callback callback);
}
