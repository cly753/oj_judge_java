package oj.judge.watchcat;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

@Deprecated
public class Watchcat extends Thread {
	public Date lastTime;
	public Long timeOut;
	public Long times;
	
	public URL target;
	public String payload;

	public Thread parent; // to kill?
	
	public Watchcat(String target, Long timeOut) throws MalformedURLException {
		this.target = new URL(target);
		this.timeOut = timeOut;
	}
	
	public void poke() {
		lastTime = new Date();
	}
	
	public void terminate() {
		this.interrupt();
	}
	
	public void informRemote() {
		//
		// TODO
		//
	}
	
	@Override
	public void run() {
		try {
			while (!this.interrupted()) {
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
