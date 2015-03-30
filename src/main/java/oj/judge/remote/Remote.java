package oj.judge.remote;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import oj.judge.common.Callback;
import oj.judge.common.Conf;
import oj.judge.common.Formatter;
import oj.judge.common.Solution;

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
	
	public String getSolution() {
		return "";
		
		//
		// TODO
		//
		
//		try {
//			URL url = new URL(Conf.judgeFetchSolution());
//
//			if (Conf.debug()) System.out.println(label + url);
//
//			HttpURLConnection con = (HttpURLConnection) url.openConnection();
//			con.setRequestMethod("POST");
//			con.setUseCaches(false);
//			
//			con.setDoOutput(true);
//			
//			DataOutputStream dos = new DataOutputStream(con.getOutputStream());
//			dos.writeBytes("hello=" + URLEncoder.encode("world", "UTF-8"));
//			dos.flush(); dos.close();
//
//			int responseCode = con.getResponseCode();
//			if (Conf.debug()) System.out.println(label + "response code = " + responseCode);
//
//			String oneLine, allLine = ""; BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
//			while ((oneLine = br.readLine()) != null) allLine += oneLine; br.close();
//
//			if (Conf.debug()) System.out.println(allLine);
//			
//			return allLine;
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
	}
	
	public boolean pushResult(String result) {
		
		//
		// TODO
		//
		
//		try {
//			URL url = new URL(Conf.handleJudgeUpdateResult());
//
//			if (Conf.debug()) System.out.println(label + url);
//
//			HttpURLConnection con = (HttpURLConnection) url.openConnection();
//			con.setRequestMethod("POST");
//			con.setUseCaches(false);
//			
//			con.setDoOutput(true);
//			
//			DataOutputStream dos = new DataOutputStream(con.getOutputStream());
//			dos.writeBytes("hello=" + URLEncoder.encode("world", "UTF-8"));
//			dos.flush(); dos.close();
//
//			int responseCode = con.getResponseCode();
//			if (Conf.debug()) System.out.println(label + "response code = " + responseCode);
//
//			String oneLine, allLine = ""; BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
//			while ((oneLine = br.readLine()) != null) allLine += oneLine; br.close();
//
//			if (Conf.debug()) System.out.println(allLine);
//			
//			return true;
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return false;
	}
	
	public void terminate() {
		this.interrupt();
	}
	
	@Override
	public void run() {
		//
		// TODO
		//
		
		String solution = getSolution();
		if (solution != null)
			emit(E.NEWPROBLEM, solution);
		
		try {
			Thread.sleep(fetchInterval);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
