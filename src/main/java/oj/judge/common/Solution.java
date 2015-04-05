package oj.judge.common;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

public class Solution {
	private static final String label = "Solution::";

	public static final int CPP = 20;
	public static final int JAVA = 30;

	public Long id;
	public Long problemId;
	public Problem problem;
	public List<Result> result;
	
    // Used by judge
    public int language;
//    public final String codeClass = "Main";
    public String code;

    // Used by judge
    public Date receiveTime;
    public Date judgeTime;
    
    public Solution() {
    	result = new ArrayList<>();
    }
        
	public String toString() {
		String ret = "Solution...\nid = " + id + "\nproblemId = " + problemId + "\nlanguage = " + language;
		ret += "\ncode: \n\t" + code + "\nreceiveTime: " + receiveTime + "\njudgeTime: " + judgeTime;
		ret += "\nproblem: \n\t" + problem.toString();
//		String ret = "Solution " + id + ":";
		for (int i = 0; i < result.size(); i++) {
			ret += "\n\tresult-" + i + ": " + Formatter.toString(result.get(i).verdict);
			ret += "\n\t\t[compile output]\n" + result.get(i).compileOut;
			ret += "\n\t\t[compile error ]\n" + result.get(i).compileError;
			ret += "\n\t\t[output]\n" + result.get(i).output;
			ret += "\n\t\t[error ]\n" + result.get(i).error;
			ret += "\n\t\t[ time ] " + result.get(i).timeUsed + " ms";
			ret += "\n\t\t[memory] " + result.get(i).memoryUsed + " KBytes";
		}
		return ret;
	}

	public boolean saveSource(Path path) {
		if (Conf.debug())
			System.out.println(label + "saveSource " + path);
		
		try {
			OpenOption[] options = new OpenOption[] { WRITE, CREATE, TRUNCATE_EXISTING };
			BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("US-ASCII"), options);
			writer.write(code, 0, code.length());
			writer.close();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean judged(int caseNo) {
		return result.get(caseNo).verdict != Result.Verdict.NONE;
	}

	public JSONObject getResultJson() {
		JSONObject j = new JSONObject();

		j.put("id", id);

		Result finalResult = new Result();
		finalResult.verdict = Result.Verdict.AC;
		String detail = "Your solution is awesome!";
		for (Result r : result) {
			if (r.verdict != Result.Verdict.AC) {
				finalResult = r;
				detail = "Almost there!";

				if (r.verdict == Result.Verdict.CE) {
					detail = r.compileError;
				}
				break;
			}
			finalResult.timeUsed = Math.max(finalResult.timeUsed, r.timeUsed);
			finalResult.memoryUsed = Math.max(finalResult.memoryUsed, r.memoryUsed);
		}
		j.put("result", finalResult.toInt());
		j.put("time", finalResult.timeUsed);
		j.put("memory", finalResult.memoryUsed);
		j.put("detail", detail);

		return j;
	}
	
	public static Solution getFakeSolution() {
		Solution solution = new Solution();
		solution.language = CPP;
		
		Problem problem = new Problem(0L, "", null);
		problem.timeLimit = 1;
		problem.memoryLimit = 1024;
		problem.input = new ArrayList<>();
		problem.output = new ArrayList<>();
		problem.input.add("1 2");
		problem.output.add("3");
		problem.input.add("7 7");
		problem.output.add("14");
		problem.totalCase = 2;
		
		solution.id = 0L;
		solution.problemId = 0L;
		solution.problem = problem;
		solution.receiveTime = new Date();
		solution.judgeTime = new Date();

		try {
			BufferedReader br = new BufferedReader(new FileReader(Paths.get("./test/main.cpp").toFile()));
			solution.code = ""; String s; while ((s = br.readLine()) != null) solution.code += s + "\n";
		} catch (IOException e) {
			e.printStackTrace();
		}

		return solution;
	}
}
