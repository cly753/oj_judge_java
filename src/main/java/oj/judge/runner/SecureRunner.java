//
// reference
// http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking
//

package oj.judge.runner;

import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;

public class SecureRunner {		
	private static ThreadMXBean bean = ManagementFactory.getThreadMXBean();
	private static String dumpPath;
	private static String result;
	private static long startTimeMs;
	private static long endTimeMs;
	
	public void main(String[] args) {
		// 
		// 0. run id
		// 1. path to security policy
		// 2. path to dump file
		// 
		
		if (args.length < 3)
			return ;
		
		installSecurityManager(args[1]);
		dumpPath = args[2] + "/" + Long.decode(args[0]);
		try {
			startTimeMs  = bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
			
			judge();
			
			endTimeMs    = bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
			result = "{\"TIME\":" + String.valueOf(endTimeMs - startTimeMs) + "}";
		} catch (Exception e) {
			result = "{\"ERROR\":" + e.getClass().getSimpleName() + "}";
		}
		
		uninstallSecurityManager();
		dump(result);
	}
	
	public void judge() {
		Main.main(null);
	}
	
	public void dump(String msg) {
		try {
			OpenOption[] options = new OpenOption[] { WRITE, CREATE_NEW, TRUNCATE_EXISTING };
			BufferedWriter writer = Files.newBufferedWriter(Paths.get(dumpPath), Charset.forName("US-ASCII"), options);
		    writer.write(msg, 0, msg.length());
		    writer.close();
		} catch (IOException x) {
			
		}
	}
	
	public void installSecurityManager(String path) {
		//
		// TODO
		// 
	}
	
	public void uninstallSecurityManager() {
		//
		// TODO
		// 
	}
}
