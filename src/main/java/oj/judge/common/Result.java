package oj.judge.common;

import org.json.JSONObject;

public class Result {
    public enum Verdict { NONE, QU, AC, PE, WA, CE, RE, TL, ML, OL, SE, RF, CJ, JE };

//    public JSONObject runnerResult; // for wrapper class

    public String compileOut;
    public String compileError;
    public String output; // standard out of solution
    public String error; // standard error of solution
    public String metrics;

    public Verdict verdict; //

    public int timeUsed;
    public int memoryUsed;

    public Result() {
        compileOut = "";
        compileError = "";
        output = "";
        error = "";
        metrics = "";

        verdict = Verdict.NONE;

        timeUsed = 0;
        memoryUsed = 0;
    }

    public int toInt() {
        int c = 503;
        switch (verdict) {
            case NONE:case QU:case SE: case CJ:case JE: break;
            case AC: c = 200; break;
            case PE: c = 301; break;
            case WA: c = 300; break;
            case CE: c = 400; break;
            case RE: c = 401; break;
            case TL: c = 402; break;
            case ML: c = 403; break;
            case OL: c = 404; break;
            case RF: c = 405; break;
        }
        return c;
    }
}

// Not Judged (NONE):
//
// Judge Error (JE):
//
//
// UVa
//
// Verdict information
// Your program will be compiled and run in our system, and the automatic judge will test it with some inputs and outputs, or perhaps with a specific judge tool. After some seconds or minutes, you'll receive by e-mail (or you'll see in the web) one of these answers:
//
// In Queue (QU): The judge is busy and can't attend your submission. It will be judged as soon as possible.
//
// Accepted (AC): OK! Your program is correct! It produced the right answer in reasoneable time and within the limit memory usage. Congratulations!
//
// Presentation Error (PE): Your program outputs are correct but are not presented in the correct way. Check for spaces, justify, line feeds...
//
// Wrong Answer (WA): Correct solution not reached for the inputs. The inputs and outputs that we use to test the programs are not public so you'll have to spot the bug by yourself (it is recomendable to get accustomed to a true contest dynamic ;-)). If you truly think your code is correct, you can contact us using the link on the left. Judge's ouputs are not always correct...
//
// Compile Error (CE): The compiler could not compile your program. Of course, warning messages are not error messages. The compiler output messages are reported you by e-mail.
//
// Runtime Error (RE): Your program failed during the execution (segmentation fault, floating point exception...). The exact cause is not reported to the user to avoid hacking. Be sure that your program returns a 0 code to the shell. If you're using Java, please follow all the submission specifications.
//
// Time Limit Exceeded (TL): Your program tried to run during too much time; this error doesn't allow you to know if your program would reach the correct solution to the problem or not.
//
// Memory Limit Exceeded (ML): Your program tried to use more memory than the judge allows. If you are sure that such problem needs more memory, please contact us.
//
// Output Limit Exceeded (OL): Your program tried to write too much information. This usually occurs if it goes into a infinite loop.
//
// Submission Error (SE): The submission is not sucessful. This is due to some error during the submission process or data corruption.
//
// Restricted Function (RF): Your program is trying to use a function that we considered harmful to the system. If you get this verdict you probably know why...
//
// Can't Be Judged (CJ): The judge doesn't have test input and outputs for the selected problem. While choosing a problem be careful to ensure that the judge will be able to judge it!
//