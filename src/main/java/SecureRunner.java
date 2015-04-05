//
// reference
// http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking
//

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
	private static String resultFile;
	private static String result;
	private static long startTimeNanoSecond;
	private static long endTimeNanoSecond;

	private static final boolean DEBUG = true;

	public static void main(String[] args) throws IOException {
		// 
		// 0. path to result file
		// 1. path to security policy
		// 

		if (DEBUG) if (args != null && args.length > 0) for (int i = 0; i < args.length; i++) System.out.println("args[" + i + "] = " + args[i]);
		if (args.length < 2)
			return ;

		resultFile = args[0];

		installSecurityManager(args[1]);

		try {
			startTimeNanoSecond  = bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;

//			Main.main(new String[0]);

			endTimeNanoSecond    = bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
			result = "{\"TIME\":" + String.valueOf(endTimeNanoSecond - startTimeNanoSecond) + "}";
		} catch (SecurityException e) {
			result = "{\"ERROR\":" + e.getClass().getSimpleName() + "}";
		} catch (Exception e) {
			result = "{\"ERROR\":" + e.getClass().getSimpleName() + "}";
		}

		uninstallSecurityManager();
		save(result);

		if (DEBUG) System.out.println("result: " + result);
	}

	public static void save(String msg) throws IOException {
		if (DEBUG) System.out.println("writing message to " + resultFile + ": " + msg);

		OpenOption[] options = new OpenOption[] { WRITE, CREATE_NEW, TRUNCATE_EXISTING };
		BufferedWriter writer = Files.newBufferedWriter(Paths.get(resultFile), Charset.forName("US-ASCII"), options);
		writer.write(msg, 0, msg.length());
		writer.close();

	}

	public static void installSecurityManager(String path) {
		//
		// TODO
		// 
	}

	public static void uninstallSecurityManager() {
		//
		// TODO
		// 
	}
}
