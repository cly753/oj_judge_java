package oj.judge.remote;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import oj.judge.common.Callback;
import oj.judge.common.Conf;
import oj.judge.common.Solution;
import org.json.JSONObject;

public class Remote extends Thread {
	private static final String label = "Remote::";
	
	public enum E { NEWPROBLEM, ERROR }
	public Map<E, Callback> listener = new HashMap<>();
	
	public void reg(E e, Callback c) {
		listener.put(e, c);
	}
	public void emit(E e, Object o) {
		listener.get(e).o = o;
		listener.get(e).call();
	}
	
	public long fetchInterval;
	
	public Remote(long interval) {
		this.fetchInterval = interval;
	}
	
	public Solution getSolution() {
		Solution solution = new Solution();
		try {
			judgeFetchSolution(solution);
			getProblemResourcesHash(solution);
			solution.problem.problemResourcesZip = getProblemResourcesZip(solution.problemId);

			if (Conf.debug()) {
				Path zipPath = Paths.get(Conf.runningPath() + "/" + solution.id + "/resource.zip");
				FileOutputStream fos = new FileOutputStream(zipPath.toFile());
				fos.write(solution.problem.problemResourcesZip);
				fos.close();
			}

			solution.receiveTime = new Date();
		} catch (IOException e) {
			e.printStackTrace();
			solution = null;
		}
		return solution;
	}

	public void judgeFetchSolution(Solution solution) throws MalformedURLException, UnsupportedEncodingException {
		if (solution == null)
			return ;

		JSONObject data = new JSONObject();
		data.put("languages", Arrays.asList("20"));

		HashMap<String, String> header = new HashMap<>();
		header.put("Content-type", "application/json");
		byte[] raw = post(Conf.judgeFetchSolution(), data.toString(), header);

		if (Conf.debug()) System.out.println(label + "judgeFetchSolution: " + new String(raw));

		if (raw == null) {
			solution = null;
			return ;
		}
		JSONObject res = new JSONObject(new String(raw));
		JSONObject resData = res.getJSONObject("data");
		if (res.has("message"))
			solution = null;
		else {
			solution.id = resData.getLong("solution");
			solution.problemId = resData.getLong("problem");
			solution.problem.resourcesHash = resData.getString("problem_hash");
			solution.code = resData.getString("code");
			solution.language = resData.getInt("language");
		}
	}

	public void getProblemResourcesHash(Solution solution) {

	}

	public byte[] getProblemResourcesZip(long problemId) throws IOException {
		return post(Conf.getProblemResourcesZip(problemId), null, null);
	}

	public boolean pushSolution(Solution solution) {
		try {
			return handleJudgeUpdateResult(solution);
		} catch (UnsupportedEncodingException | MalformedURLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean handleJudgeUpdateResult(Solution solution) throws UnsupportedEncodingException, MalformedURLException {
		JSONObject toPush = solution.getResultJson();
		if (Conf.debug()) System.out.println(label + "toPush: " + toPush.toString());

		HashMap<String, String> header = new HashMap<>();
		header.put("Content-type", "application/json");
		JSONObject res = new JSONObject(new String(post(Conf.handleJudgeUpdateResult(), URLEncoder.encode(toPush.toString(), "UTF-8"), header)));

		if (Conf.debug()) System.out.println(label + "handleJudgeUpdateResult: " + res.toString());
		return true;
	}

	public byte[] post(URL url, String data, Map<String, String> header) {
		try {
			url = new URL(url.toString() + "?" + "judge=" + Conf.judgeAccessName() + "&secret=" + Conf.judgeAccessSecret());
			if (Conf.debug()) System.out.println(label + url);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			if (header != null) {
				for (Map.Entry<String, String> h : header.entrySet()) {
					con.setRequestProperty(h.getKey(), h.getValue());
				}
			}
			con.setRequestMethod("POST");
			con.setUseCaches(false);

			if (Conf.debug()) System.out.println(label + "data: " + data);
			if (data != null) {
				con.setDoOutput(true);
				DataOutputStream dos = new DataOutputStream(con.getOutputStream());
				dos.writeBytes(data);
				dos.flush(); dos.close();
			}

			int responseCode = con.getResponseCode();
			if (Conf.debug()) System.out.println(label + "response code = " + responseCode);

			if (responseCode == 400)
				return null;

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
			int c; byte[] temp = new byte[512];
			while ((c = bis.read(temp, 0, 512)) != -1)
				buffer.write(temp, 0, c);

			temp = buffer.toByteArray();
			bis.close(); buffer.close();
			return temp;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void terminate() {
		this.interrupt();
	}
	
	@Override
	public void run() {
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
