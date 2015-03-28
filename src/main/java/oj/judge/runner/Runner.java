package oj.judge.runner;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oj.judge.common.Callback;
import oj.judge.common.Solution;

public class Runner extends Thread {
	private static final String label = "Runner::";
	
	public enum E { ERROR, FINISH };
	public Map<E, Callback> listener;
	
	public void reg(E e, Callback c) {
		listener.put(e, c);
	}
	public void emit(E e) {
		listener.get(e).call();
	}
	
	@Override
	public void run() {
		prepare();
		execute();
		cleanUp();
		checker.check(solution, resultFile);

		emit(E.FINISH);
	}
	
	public Integer id;
	public Solution solution;

	public Path runningPath;
	public Path inputFile;
	public Path resultFile;
	public Path securityPolicy;
	
	public static int timeOut = 2000;
	public static int outputBufferSize = 10000000;

	public Checker checker;

	public Runner(Integer id, Path runningPath, Solution solution, Checker checker) {
		this.id = id;
		this.listener = new HashMap<E, Callback>();
		
		this.runningPath = runningPath;
		this.resultFile  = Paths.get(runningPath + "/" + id.toString());
		this.securityPolicy = Paths.get(runningPath + "/" + "x");
		this.inputFile   = Paths.get(runningPath + "/" + "in");
		
		this.solution    = solution;
		this.checker     = checker;
	}

	public boolean prepare() {
		if (solution.judged)
			return true;

		boolean result = false; 
		try {
			result = solution.compileSave(runningPath);
			if (result) {
				delete(resultFile.toFile());
				
				OpenOption[] options = new OpenOption[] { WRITE, CREATE, TRUNCATE_EXISTING };
				BufferedWriter writer = Files.newBufferedWriter(inputFile, Charset.forName("US-ASCII"), options);
			    writer.write(solution.problem.input, 0, solution.problem.input.length());
			    writer.close();
			}
			else {
				solution.result = Solution.Result.CE;
				solution.judged = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return result;
	}
	public boolean cleanUp() {
		boolean ok = true;
		ok &= delete(inputFile.toFile());
		ok &= delete(Paths.get(runningPath + "/" + solution.classToRun + ".class").toFile());
//		ok &= delete(Paths.get(runningPath + "/" + "SecureRunner" + ".class").toFile());
		return ok;
	}
	public boolean delete(File f) {
		return (f.exists() && !f.isDirectory()) ? f.delete() : false;
	}
	
	public boolean execute() {
		if (solution.judged)
			return true;
		
		List<String> cmd = Arrays.asList("java", "-cp", runningPath.toString(), "SecureRunner", resultFile.toString(), securityPolicy.toString());
		ProcessBuilder pb = new ProcessBuilder(cmd);
		
		pb.redirectInput(inputFile.toFile());
		
		try {
			Process p = pb.start();
			
			BufferedReader output = new BufferedReader (new InputStreamReader(p.getInputStream()), outputBufferSize);
			BufferedReader error = new BufferedReader (new InputStreamReader(p.getErrorStream()), outputBufferSize);
			
			Thread.sleep(timeOut);
			if (p.isAlive()) {
				p.destroyForcibly();
				solution.result = Solution.Result.TL;
				solution.judged = true;
			}
			else {
				String outputRead = readBR(output);
				String errorRead  = readBR(error);

			    if (outputRead.length() == outputBufferSize) {
					solution.result = Solution.Result.OL;
					solution.judged = true;
				}
				else {
					solution.output = outputRead;
				}
			    
			    System.out.println("output: ----\n" + outputRead + "------------");
			    System.out.println("error : ----\n" + errorRead  + "------------");
			}
		    
		    output.close();
		    error.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public String readBR(BufferedReader br) throws IOException {
		char[] buffer = new char[outputBufferSize];
		int actualRead = br.read(buffer, 0, outputBufferSize);
		if (actualRead == -1)
			return "";
		else
			return new String(buffer, 0, actualRead);
	}
}
