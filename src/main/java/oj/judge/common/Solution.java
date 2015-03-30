package oj.judge.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
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
	public enum Result { NONE, QU, AC, PE, WA, CE, RE, TL, ML, OL, SE, RF, CJ, JE };
	
	public Long id = 0L;
	public Long problemId = 0L;
	public Problem problem;
	
    // Used by judge
    public int language = 0;
    public final String codeClass = "Main";
    public String code = "public class Main { public static void main(String[] args) { System.out.println(\"hello judge!\"); } }";
    public static final String secureRunnerClass = "SecureRunner";
    public static final String secureRunner = "import static java.nio.file.StandardOpenOption.*;import java.io.*;import java.lang.management.*;import java.nio.charset.Charset;import java.nio.file.*;public class SecureRunner {   private static ThreadMXBean bean = ManagementFactory.getThreadMXBean(); private static String resultFile; private static String result; private static long startTimeNanoSecond; private static long endTimeNanoSecond;   public static void main(String[] args) throws IOException {  installSecurityManager(args[1]);    try {   startTimeNanoSecond  = bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;      Main.main(new String[0]);      endTimeNanoSecond = bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;   result = \"{\\\"TIME\\\":\" + String.valueOf(endTimeNanoSecond - startTimeNanoSecond) + \"}\";  } catch (SecurityException e) {   result = \"{\\\"ERROR\\\":\" + e.getClass().getSimpleName() + \"}\";  } catch (Exception e) {   result = \"{\\\"ERROR\\\":\" + e.getClass().getSimpleName() + \"}\";  }    uninstallSecurityManager();  OpenOption[] options = new OpenOption[] { WRITE, CREATE_NEW, TRUNCATE_EXISTING };  BufferedWriter writer = Files.newBufferedWriter(Paths.get(args[0]), Charset.forName(\"US-ASCII\"), options);  writer.write(result, 0, result.length());  writer.close(); }  public static void installSecurityManager(String path) { }  public static void uninstallSecurityManager() { }}";

    // Used by judge
    public Date receiveTime = new Date();
    public Date judgeTime = new Date();
    public JSONObject runnerResult = null;

    public Result result = Result.NONE;
    public String additionalResult = "";
    
    public double timeUsed = 0;
    public double memoryUsed = 0;
    
    public String output = "This is output from solution.";
    public String error = "";
    
    public Solution(Problem problem) {
    	this.problem = problem;
		//
		// TODO
		// 
    }
    
    public boolean judged() {
    	return result != Result.NONE;
    }
    
    public boolean compileSave(Path path) throws IOException {
    	JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    	Iterable<? extends JavaFileObject> fileObjects = getFileObjects();
    	DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    	StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
    	compiler.getTask(null, fileManager, diagnostics, null, null, fileObjects).call();
    	
    	if (Conf.debug())
	        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics())
	            System.out.format("Error on line %d:%d in %s: %s%n",
	                              diagnostic.getLineNumber(),
	                              diagnostic.getColumnNumber(),
	                              diagnostic.getSource().toUri(),
	                              diagnostic.getMessage(Locale.ENGLISH));
    	
    	if (diagnostics.getDiagnostics().size() > 0)
    		return false; // compilation error
    	
        //
        // TODO
    	// side effect ?
        // creating .class when new FileInputStream(classToRun + ".class") ??
        //
		FileInputStream fis = new FileInputStream(codeClass + ".class");
        FileOutputStream fos = new FileOutputStream(path + "/" + codeClass + ".class");
        fos.write(IOUtils.toByteArray(fis));
        fis.close();
        fos.close();
        
        //
        // TODO
        // drop wrapper class
        //
        fis = new FileInputStream(secureRunnerClass + ".class");
        fos = new FileOutputStream(path + "/" + secureRunnerClass + ".class");
        fos.write(IOUtils.toByteArray(fis));
        fis.close();
        fos.close();
        
        return true;
    }
    private Iterable<JavaFileObject> getFileObjects() {
    	return Arrays.asList(
    			(JavaFileObject)new SolutionJavaFileObject(codeClass, this.code)
    			, (JavaFileObject)new SolutionJavaFileObject(secureRunnerClass, Solution.secureRunner)
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
        
	public String toString() {
		return "Solution " + id + ": result = " + Formatter.toString(result);
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