package oj.judge.runner;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
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
import oj.judge.common.Conf;
import oj.judge.common.Solution;

import org.json.JSONObject;

public class Runner extends Thread {
	private static final String label = "Runner::";

	public enum E { ERROR, FINISH };
	public Map<E, Callback> listener;

	public void reg(E e, Callback c) {
		listener.put(e, c);
	}
	public void emit(E e, Object o) {
		listener.get(e).o = o;
		listener.get(e).call();
	}

	@Override
	public void run() {
		prepare();
		execute();
//		cleanUp();
		checker.check(solution);

		emit(E.FINISH, solution);
	}

	public Integer id;
	public Solution solution;

	public Path runningPath;
	public Path securityPolicy;
	public Path inputFile;
	public Path outputFile;
	public Path errorFile;
	public Path resultFile;

	public long timeOut;
	public int bufferSize;

	public Checker checker;

	public Runner(Integer id, Path runningPath, Solution solution, Checker checker) {
		this.id = id;
		this.listener = new HashMap<E, Callback>();

		this.timeOut = Conf.timeOut();
		this.bufferSize = Conf.bufferSize();
		
		this.runningPath = runningPath;
		this.securityPolicy = Conf.securityPolicyFile();
		this.inputFile   = Paths.get(runningPath + "/" + "in");
		this.outputFile  = Paths.get(runningPath + "/" + "out");
		this.errorFile   = Paths.get(runningPath + "/" + "error");
		this.resultFile  = Paths.get(runningPath + "/" + id.toString());

		this.solution    = solution;
		this.checker     = checker;
	}

	public void prepare() {
		if (solution.judged())
			return ;

		try {
			boolean ok = solution.compileSave(runningPath);
			if (!ok)
				solution.result = Solution.Result.CE;
		} catch (IOException e) {
			solution.result = Solution.Result.JE;
		}

		try {
			if (solution.judged())
				return ;

			OpenOption[] options = new OpenOption[] { WRITE, CREATE, TRUNCATE_EXISTING };
			BufferedWriter writer = Files.newBufferedWriter(inputFile, Charset.forName("US-ASCII"), options);
			writer.write(solution.problem.input, 0, solution.problem.input.length());
			writer.close();
			
			writer = Files.newBufferedWriter(outputFile, Charset.forName("US-ASCII"), options);
			writer.close();
			
			writer = Files.newBufferedWriter(errorFile, Charset.forName("US-ASCII"), options);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			solution.result = Solution.Result.JE;
		}
	}
	public boolean cleanUp() {
		boolean ok = true;
		ok &= delete(inputFile.toFile());
		ok &= delete(outputFile.toFile());
		ok &= delete(errorFile.toFile());
		ok &= delete(resultFile.toFile());
		ok &= delete(Paths.get(runningPath + "/" + solution.codeClass + ".class").toFile());
		//ok &= delete(Paths.get(runningPath + "/" + "SecureRunner" + ".class").toFile());
		return ok;
	}
	public boolean delete(File f) {
		return (f.exists() && !f.isDirectory()) ? f.delete() : false;
	}

	public void execute() {
		if (solution.judged())
			return ;
		
		applyLinux();

		List<String> cmd = Arrays.asList("java"
				, "-client"
				, "-Xmx" + (int)(solution.problem.memoryLimit + 10) + "m"
				, "-Xss64m"
				, "-cp"
				, runningPath.toString()
				, "SecureRunner"
				, resultFile.toString()
				, securityPolicy.toString()
				);
		
		ProcessBuilder pb = new ProcessBuilder(cmd);

		pb.redirectInput(inputFile.toFile());
		pb.redirectOutput(outputFile.toFile());
		pb.redirectError(errorFile.toFile());

		try {
			Process p = pb.start();

//			BufferedReader output = new BufferedReader (new InputStreamReader(p.getInputStream()), bufferSize);
//			BufferedReader error = new BufferedReader (new InputStreamReader(p.getErrorStream()), bufferSize);

			Thread.sleep(timeOut);
			if (p.isAlive()) {
				p.destroyForcibly();
				solution.result = Solution.Result.TL;
			}

			if (solution.judged())
				return ;

			solution.error = Files.lines(errorFile).reduce("", (a, b) -> a + '\n' + b);
			if (solution.error.length() > 0) {
				if (Conf.debug()) System.out.println("error : ----\n" + solution.error  + "\n------------");
				solution.result = Solution.Result.JE;
				return ;
			}
			
//			String outputRead = readBR(output);
//			String errorRead  = readBR(error);
//			output.close();
//			error.close();
			 
//			if (outputRead.length() == bufferSize) {
//				solution.result = Solution.Result.OL;
//			}
			if (outputFile.toFile().length() > bufferSize) {
				solution.result = Solution.Result.OL;
			}
			else {
//				solution.output = outputRead;
				solution.output = Files.lines(outputFile).reduce("", (a, b) -> a + '\n' + b);
				solution.runnerResult = new JSONObject(Files.lines(resultFile).reduce("", String::concat));
			}

			if (Conf.debug()) System.out.println("output: ----\n" + solution.output + "\n------------");

		} catch (IOException e) {
			e.printStackTrace();
			solution.result = Solution.Result.JE;
		} catch (InterruptedException e) {
			e.printStackTrace();
			solution.result = Solution.Result.JE;
		}
	}
	
	public String readBR(BufferedReader br) throws IOException {
		char[] buffer = new char[bufferSize];
		int actualRead = br.read(buffer, 0, bufferSize);
		if (actualRead == -1)
			return "";
		else
			return new String(buffer, 0, actualRead);
	}
	
	public void applyLinux() {
		
	}
}
