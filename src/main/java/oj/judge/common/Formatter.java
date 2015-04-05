package oj.judge.common;

public class Formatter {
    
    public static String toString(Result.Verdict result) {
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