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
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

public class Solution {
	public enum Language { JAVA, CPP };

	public Long id;
	public Long problemId;
	public Problem problem;
	public List<Result> result;
	
    // Used by judge
    public Language language = Language.JAVA;
    public final String codeClass = "Main";
//    public String code = "public class Main { public static void main(String[] args) { System.out.println(\"hello judge!\"); } }";
    public String code = "#include <iostream> \n using namespace std; int main(int arg, char* args[]) { cout << 1234 << endl; }";
    
    // Used by judge
    public Date receiveTime;
    public Date judgeTime;
    
    public Solution() {
		//
		// TODO
		// 
    }
        
	public String toString() {
		String ret = "Solution " + id + ":";
		for (int i = 0; i < result.size(); i++)
			ret += " result-" + i + ": " + Formatter.toString(result.get(i).verdict);
		return ret;
	}

	public boolean saveSource(Path path) {
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
}
