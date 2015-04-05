package oj.judge.common;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public static Path runningPath() {
		return Paths.get(conf.getString("running_path"));
	}
	
	public static boolean debug() {
		return conf.getBoolean("debug");
	}
	
	public static long fetchInterval() {
		return conf.getLong("fetch_interval");
	}
	
	public static int outputLimit() {
		return conf.getInt("buffer_size");
	}
	
	public static int maxExtraTime() {
		return conf.getInt("max_time");
	}
	public static int maxMemory() {
		return conf.getInt("max_memory");
	}
	
	public static Path securityPolicyFile() {
		return Paths.get(conf.getString("security_policy_file"));
	}

	public static String getRemoteSocket() {
		return conf.getString("remote_socket");
	}

	public static String judgeAccessName() {
		return conf.getString("judgeAccessName");
	}
	public static String judgeAccessSecret() {
		return conf.getString("judgeAccessSecret");
	}

	public static URL judgeFetchSolution() {
		try {
			return new URL(getRemoteSocket() + conf.getString("judgeFetchSolution"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static URL handleJudgeUpdateResult() {
		try {
			return new URL(getRemoteSocket() + conf.getString("handleJudgeUpdateResult"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static URL getProblemResourcesHash(long id) { 
		try {
			return new URL(getRemoteSocket() + conf.getString("getProblemResourcesHash").replace(":id", "" + id));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static URL getProblemResourcesZip(long id) {
		try {
			return new URL(getRemoteSocket() + conf.getString("getProblemResourcesZip").replace(":id", "" + id));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Path compileScript() {
		return Paths.get(conf.getString("compile_script"));
	}
	public static Path compileName() {
		return Paths.get(conf.getString("compile_name"));
	}
	public static Path runScript() {
		return Paths.get(conf.getString("run_script"));
	}


}
