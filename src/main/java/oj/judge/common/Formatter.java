package oj.judge.common;

import org.json.JSONObject;


/**
 * Created by t_chenli on 3/27/2015.
 */
public class Formatter {

    public static String toResponse(Solution solution) {
    	JSONObject jo = new JSONObject("{}");
    	
    	return "";
    }
    
    public static Solution toSolution(String solution) {
    	JSONObject jo = new JSONObject("{}");
    	
    	return new Solution(new Problem());
    }
    
    public static String toString(Solution.Result result) {
		switch (result) {
		case NONE:
			return "NONE";
		case JE:
			return "Judge Error";
		case QU:
			return "In Queue";
		case AC:
			return "Accepted";
		case PE:
			return "Presentation Error";
		case WA:
			return "Wrong Answer";
		case CE:
			return "Compile Error";
		case RE:
			return "Runtime Error";
		case TL:
			return "Time Limit Exceeded";
		case ML:
			return "Memory Limit Exceeded";
		case OL:
			return "Output Limit Exceeded";
		case SE:
			return "Submission Error";
		case RF:
			return "Restricted Function";
		case CJ:
			return "Can't Be Judged";
		default:
			return "UNDEFINED";
		}
    }
}