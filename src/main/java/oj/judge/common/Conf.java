package oj.judge.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.json.JSONObject;

public class Conf {
	private static final String label = "Conf::";

	public static final String configurePath = "./configure.json";
	private static JSONObject conf = null;
	
	public static boolean init() {
		System.out.println(label + "Configure File: " + new File(configurePath).getAbsolutePath());
		
		try {
			String raw = Files.lines(Paths.get(configurePath)).reduce("", String::concat);
			conf = new JSONObject(raw);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
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
	
	public static String judgeFetchSolution() {
		return "http://localhost:9000/judge/fetch";
	}
	public static String handleJudgeUpdateResult() {
		return "http://localhost:9000/judge/update";
	}
}
