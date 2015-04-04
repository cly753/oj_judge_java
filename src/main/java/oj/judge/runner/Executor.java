package oj.judge.runner;

import oj.judge.common.Conf;
import oj.judge.common.Result;
import oj.judge.common.Solution;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by cly on 4/4/15.
 */
public class Executor {
    private static final String label = "Executor::";

    private int language;
    private double timeLimit;
    private double memoryLimit;
    private Path exe;
    private Path stdin;
    private Path stdout;
    private Path stderr;
    private Path timeout;
    private Path memoryout;

    public Executor(int a, double b, double c, Path d, Path e, Path f, Path g, Path h, Path i) {

        language = a;
        timeLimit = b;
        memoryLimit = c;
        exe = d;
        stdin = e;
        stdout = f;
        stderr = g;
        timeout = h;
        memoryout = i;

    }
    public Result.Verdict execute() {
        ProcessBuilder pb = getProcessBuilder(language, stdin, stdout, stderr, timeout);

        if (Conf.debug()) System.out.print(label + "ProcessBuilder... ");
        if (Conf.debug()) for (String s : pb.command()) System.out.print(s + " ");
        if (Conf.debug()) System.out.println();

        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.Verdict.JE;
        }

        try {
//            final Runner runnerHandle = this;
            Thread watcher = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep((long) (timeLimit + Conf.timeOut()));
//                        runnerHandle.interrupt();
                    } catch (InterruptedException e) {

                    }
                }
            };
            watcher.start();
            p.waitFor();
            watcher.interrupt();
        } catch (InterruptedException e) {
            if (Conf.debug()) System.out.println(label + "Runner interrupted due to timeout.");
            p.destroyForcibly();
            try {

                if (System.getProperty("os.name").contains("Windows")) {
                    Runtime.getRuntime().exec("taskkill /F /IM solution.exe");
                }
                else {

                }

            } catch (IOException e1) {
                e1.printStackTrace();
                return Result.Verdict.JE;
            }
            return Result.Verdict.TL;
        }

        return Result.Verdict.NONE;
    }

    private ProcessBuilder getProcessBuilder(int language, Path input, Path output, Path error, Path metrics) {
        String scriptPath = Conf.runScript().toAbsolutePath().toString();
        String suffixScript;
        String suffixExe;

        if (Conf.debug()) System.out.println(System.getProperty("os.name"));

        if (System.getProperty("os.name").contains("Windows")) {
            suffixScript = ".bat";
            suffixExe = ".exe";
        }
        else {
            suffixScript = ".sh";
            suffixExe = "";
        }

        switch (language) {
            case Solution.CPP:
                scriptPath = scriptPath + "/CPP" + suffixScript;
                break;
            case Solution.JAVA:
                scriptPath = scriptPath + "/JAVA" + suffixScript;
                break;
            default:
                return null;
        }

        return new ProcessBuilder(
                scriptPath,
                exe.toAbsolutePath().toString() + suffixExe,
                input.toAbsolutePath().toString(),
                output.toAbsolutePath().toString(),
                error.toAbsolutePath().toString(),
                metrics.toAbsolutePath().toString(),
                "" + (Conf.outputLimit() / 1024)
        );
    }
}
