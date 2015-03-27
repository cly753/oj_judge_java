package oj.judge.watchcat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import oj.judge.center.IWatchcat;

public class Watchcat implements IWatchcat, Runnable {
	public Date lastTime;
	public Long timeOut;
	public Long times;
	
	public URL target;
	public String payload;

	public Thread running;
	public Thread parent; // to kill?
	
	public Watchcat(String target, Long timeOut) throws MalformedURLException {
		this.target = new URL(target);
		this.timeOut = timeOut;
	}
	
	public void watch() {
		running = new Thread(this);
		running.start();
	}
	
	public void poke() {
		lastTime = new Date();
	}
	
	public void terminate() {
		running.interrupt();
	}
	
	public void informRemote() {
		//
		// TODO
		// HTTP REQUEST
		//
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				informRemote();
				
				Thread.sleep(timeOut);
				if (lastTime.getTime() - new Date().getTime() > timeOut)
					return ;
			}
		} catch (InterruptedException e) {
			//
			// exit
			//
			e.printStackTrace();
		}
	}
}
