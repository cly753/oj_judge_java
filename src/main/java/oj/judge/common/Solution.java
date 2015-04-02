package oj.judge.common;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

public class Solution {
	private static final String label = "Solution::";
	
	public enum Language { JAVA, CPP };

	public Long id;
	public Long problemId;
	public Problem problem;
	public List<Result> result;
	
    // Used by judge
    public Language language;
    public final String codeClass = "Main";
//    public String code = "public class Main { public static void main(String[] args) { System.out.println(\"hello judge!\"); } }";
    public String code = "#include <iostream> \n using namespace std; int main(int arg, char* args[]) { int a, b; cin >> a >> b; cout << a + b << endl; while (1); return 0; }";
    
    // Used by judge
    public Date receiveTime;
    public Date judgeTime;
    
    public Solution() {
    	result = new ArrayList<Result>();
    }
        
	public String toString() {
		String ret = "Solution " + id + ":";
		for (int i = 0; i < result.size(); i++)
			ret += "\n\tresult-" + i + ": " + Formatter.toString(result.get(i).verdict);
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

		return null;
	}
	
	public static Solution getFakeSolution() {
		Solution solution = new Solution();
		solution.language = Language.CPP;
		
		Problem problem = new Problem(0L, null, 1, 100, null, false);
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

		for (int i = 0; i < problem.totalCase; i++)
			solution.result.add(new Result());

		return solution;
	}
}
