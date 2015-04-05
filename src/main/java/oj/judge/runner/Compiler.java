package oj.judge.runner;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import oj.judge.common.Conf;
import oj.judge.common.Solution;

public class Compiler {
    private static final String label = "Compiler::";
    public static boolean compile(int language, Path source, Path out, Path compileOut, Path compileError) throws IOException {
        String scriptPath = Conf.compileScript().toAbsolutePath().toString();
        String suffixScript;
        
        if (Conf.debug()) System.out.println(System.getProperty("os.name"));

        if (System.getProperty("os.name").contains("Windows")) {
        	suffixScript = ".bat";
        	out = Paths.get(out.toAbsolutePath() + ".exe");
        }
        else {
        	suffixScript = ".sh";
        }
        
        switch (language) {
            case Solution.CPP:
                scriptPath = scriptPath + "/CPP" + suffixScript;
                break;
            case Solution.JAVA:
                scriptPath = scriptPath + "/JAVA.sh" + suffixScript;
                break;
            default:
                return false;
        }
        ProcessBuilder pb = new ProcessBuilder(
                scriptPath,
                source.toString(),
                out.toString(),
                compileOut.toString(),
                compileError.toString()
        );
        Process p = pb.start();
		try {
			int returnCode = p.waitFor();

            if (Conf.debug()) System.out.println(label + "returnCode = " + returnCode);
            return returnCode == 0;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
    }

//SOURCE=$1
//OUT=$2
//COMOUT=$3
//COMERROR=$4
}