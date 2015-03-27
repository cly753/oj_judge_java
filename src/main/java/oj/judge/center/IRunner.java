package oj.judge.center;

import java.nio.file.Path;

import oj.judge.common.Callback;
import oj.judge.common.Solution;

public interface IRunner {
	public void judge(Path runningPath, Solution solution, IChecker checker, Callback callback);
}
