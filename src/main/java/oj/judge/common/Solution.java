package oj.judge.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.IOUtils;
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
    public String code = "public class Main { public static void main(String[] args) { System.out.println(\"hello judge!\"); } }";

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