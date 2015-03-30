package oj.judge.common;

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

	public boolean judged(int caseNo) {
		return result.get(caseNo).verdict != Result.Verdict.NONE;
	}

	public JSONObject getResultJson() {

		return null;
	}
}
