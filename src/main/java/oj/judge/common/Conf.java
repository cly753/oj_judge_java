package oj.judge.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.json.JSONObject;

public class Conf {
	private static final String label = "Conf::";

	public static final String configurePath = "./configure.json";
	private static JSONObject conf;
	
	public static boolean init() {
		System.out.println(label + "Configure File: " + new File(configurePath).getAbsolutePath());
		
		try {
			conf = new JSONObject(Files.lines(Paths.get(configurePath)).reduce("", String::concat));
			remoteSocket = "http://localhost:9000";
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static Path runningPath() {
//		return Paths.get("C:/Users/" + System.getProperty("user.name") + "/Desktop");
		return Paths.get("~/Desktop");
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

	public static String remoteSocket;
	public static String judgeFetchSolution() {
		return remoteSocket + "/judge/fetch";
	}
	public static String handleJudgeUpdateResult() { return remoteSocket + "/judge/update"; }
	public static String getProblemResourcesHash(long id) { return remoteSocket + "/judge/problem/" + id + "/hash.json"; };
	public static String getProblemResourcesZip(long id) { return remoteSocket + "/judge/problem/" + id + "/package.zip"; };

	public static String compileScript() {
		return "./compile-script";
	}
	public static String compileName() {
		return "1";
	}
	public static String runScript() {
		return "./run-script";
	}
}
