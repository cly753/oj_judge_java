package oj.judge.remote;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import oj.judge.common.Callback;
import oj.judge.common.Conf;
import oj.judge.common.Solution;
import org.json.JSONObject;

public class Remote extends Thread {
	private static final String label = "Remote::";
	
	public enum E { NEWPROBLEM, ERROR };
	public Map<E, Callback> listener;
	
	public void reg(E e, Callback c) {
		listener.put(e, c);
	}
	public void emit(E e, Object o) {
		listener.get(e).o = o;
		listener.get(e).call();
	}
	
	public long fetchInterval;
	
	public Remote(long interval) {
		this.listener = new HashMap<E, Callback>();
		this.fetchInterval = interval;
	}

	public byte[] post(URL url, String data) {
		try {
			if (Conf.debug()) System.out.println(label + url);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setUseCaches(false);

			con.setDoOutput(true);

			if (data != null) {
				DataOutputStream dos = new DataOutputStream(con.getOutputStream());
				dos.writeBytes(data);
				dos.flush(); dos.close();
			}

			int responseCode = con.getResponseCode();
			if (Conf.debug()) System.out.println(label + "response code = " + responseCode);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
			int c; byte[] temp = new byte[512];
			while ((c = bis.read(temp, 0, 512)) != -1)
				buffer.write(temp, 0, c);

			temp = buffer.toByteArray();
			bis.close(); buffer.close();
			return temp;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Solution getSolution() {
		Solution solution = new Solution();
		try {
			judgeFetchSolution(solution);
			getProblemResourcesHash(solution);
			getProblemResourcesZip(solution);
			solution.receiveTime = new Date();
		} catch (MalformedURLException e) {
			e.printStackTrace();

			solution = null;
		}
		return solution;
	}

	public void judgeFetchSolution(Solution solution) throws MalformedURLException {
		if (solution == null)
			return ;

		byte[] raw = post(new URL(Conf.judgeFetchSolution()), null);
		JSONObject fetched = new JSONObject(new String(raw));

		if (fetched.has("message"))
			solution = null;
		else {
			solution.id = fetched.getLong("solution");
			solution.problemId = fetched.getLong("problem");
			solution.problem.resourcesHash = fetched.getString("problem_hash");
			solution.code = fetched.getString("code");
//			solution.language = fetched.getInt("language");
			solution.language = Solution.Language.JAVA;
		}
	}

	public void getProblemResourcesHash(Solution solution) throws MalformedURLException {
		if (solution == null)
			return ;
	}

	public void getProblemResourcesZip(Solution solution) throws MalformedURLException {
		if (solution == null)
			return ;

		byte[] raw = post(new URL(Conf.getProblemResourcesZip(solution.problem.id)), null);
		if (raw == null)
			solution = null;
		else
			solution.problem.problemResourcesZip = raw;
	}

	public boolean pushSolution(Solution solution) {
		try {
			return handleJudgeUpdateResult(solution);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean handleJudgeUpdateResult(Solution solution) throws UnsupportedEncodingException, MalformedURLException {
		JSONObject toPush = solution.getResultJson();
		JSONObject res = new JSONObject(new String(post(new URL(Conf.handleJudgeUpdateResult()), URLEncoder.encode(toPush.toString(), "UTF-8"))));
		String response = res.getString("data");
		return true;
	}
	
	public void terminate() {
		this.interrupt();
	}
	
	@Override
	public void run() {
		//
		// TODO
		//
		
		Solution solution = getSolution();
		if (solution != null)
			emit(E.NEWPROBLEM, solution);
		
		try {
			Thread.sleep(fetchInterval);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
