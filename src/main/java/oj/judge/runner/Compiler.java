package oj.judge.runner;

import oj.judge.common.Conf;
import oj.judge.common.Solution;

import org.apache.commons.io.IOUtils;

import javax.tools.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

/**
 * Created by t_chenli on 3/30/2015.
 */
public class Compiler {
    public static boolean compile(Solution.Language language, Path source, Path out, Path compileOut, Path compileError) throws IOException {
        String scriptPath = Conf.compileScript();
        switch (language) {
            case CPP:
                scriptPath = scriptPath + "/CPP.sh";
                break;
            case JAVA:
                scriptPath = scriptPath + "/JAVA.sh";
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

	        if (returnCode == 0)
	            return true;
	        else
	            return false;
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