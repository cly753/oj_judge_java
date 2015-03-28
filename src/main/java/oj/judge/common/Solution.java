package oj.judge.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.IOUtils;

public class Solution {
	public enum Result { QU, AC, PE, WA, CE, RE, TL, ML, OL, SE, RF, CJ };
	
	public Long id;
	public Long problemId;
	public Problem problem;
	
    // Used by judge
    public int language;
    public final String classToRun = "Main";
    public String code = "public class Main { public static void main(String[] args) { System.out.println(\"hello judge!\"); } }";
    private static final String secureRunner = "";

    // Used by judge
    public Date receiveTime;
    public Date judgeTime;

    public Result result;
    public String additionalResult;
    
    public int timeUsed;
    public int memoryUsed;
    
    public String output = "This is output from solution.";

    public boolean judged;
    
    public Solution(Problem problem) {
    	this.problem = problem;
    	
		//
		// TODO
		// 
    }
    
    public boolean compileSave(Path path) throws IOException {
    	JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    	Iterable<? extends JavaFileObject> fileObjects = getFileObjects();
    	DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    	StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
    	compiler.getTask(null, fileManager, diagnostics, null, null, fileObjects).call();
    	
    	if (diagnostics.getDiagnostics().size() > 0)
    		return false; // compilation error
    	
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics())
            System.out.format("Error on line %d in %s%n",
                              diagnostic.getLineNumber(),
                              diagnostic.getSource().toUri());
        
        //
        // TODO side effect
        // creating .class when new FileInputStream(classToRun + ".class") ??
        //
		FileInputStream fis = new FileInputStream(classToRun + ".class");
        FileOutputStream fos = new FileOutputStream(path + "/" + classToRun + ".class");
        fos.write(IOUtils.toByteArray(fis));
        fis.close();
        fos.close();
        
        return true;
    }
    private Iterable<JavaFileObject> getFileObjects() {
    	return Arrays.asList(
    			(JavaFileObject)new SolutionJavaFileObject(classToRun, this.code)
    			);
    }
    
    class SolutionJavaFileObject extends SimpleJavaFileObject {
    	private final String code;
		public SolutionJavaFileObject(String name, String code) {
			super(URI.create("string:///" + name + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
			this.code = code;
		}
    	
		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}
    }
        
	//
	// TODO
	// 
}


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