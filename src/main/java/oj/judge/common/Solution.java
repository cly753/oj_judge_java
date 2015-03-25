package oj.judge.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
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
	public Long id;
	public Long problemId;
	public Problem problem;
	
    // Used by judge
    public int language;
    public String code;
    private static final String secureRunner = "";

    // Used by judge
    public Date receiveTime;
    public Date judgeTime;

    public int result;
    public String judgeResponse;
    public int timeUsed;
    public int memoryUsed;

    public boolean judged;
    
    public Solution() {
		//
		// TODO
		// 
    }
    
    public void compileSave(String path) throws IOException {
    	JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    	Iterable<? extends JavaFileObject> fileObjects = getFileObjects();
    	DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
    	StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
    	compiler.getTask(null, fileManager, diagnostics, null, null, fileObjects).call();
    	
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics())
            System.out.format("Error on line %d in %s%n",
                              diagnostic.getLineNumber(),
                              diagnostic.getSource().toUri());
        
		FileInputStream fis = new FileInputStream("Main.class");
        FileOutputStream fos = new FileOutputStream("C:/Users/Liyang/Desktop/H.class");
        fos.write(IOUtils.toByteArray(fis));
        fos.close();
    }
    private Iterable<JavaFileObject> getFileObjects() {
    	return Arrays.asList(
    			(JavaFileObject)new SolutionJavaFileObject("Main", this.code)
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
