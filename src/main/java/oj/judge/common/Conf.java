package oj.judge.common;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Conf {

	public static void init() {
		
	}
	
	public static Path runningPath() {
		return Paths.get("C:/Users/" + System.getProperty("user.name") + "/Desktop");
	}
	
	public static boolean debug() {
		return true;
	}
	
	public static long fetchInterval() {
		return 2000;
	}
	
	public static int bufferSize() {
		return 100000;
	}
	
	public static int timeOut() {
		return 2000;
	}
	
	public static Path securityPolicyFile() {
		return Paths.get(".");
	}
}
