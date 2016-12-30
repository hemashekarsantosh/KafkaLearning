package com.santosh.tail.test;

import java.io.File;

import com.santosh.tail.LogFileTailer;
import com.santosh.tail.LogFileTailerListener;


public class TailTest implements LogFileTailerListener {

	private Thread t;
	
	public static void main(String[] args) {
	
		TailTest test=new TailTest();
		test.startTailing();

	}

	public void startTailing(){
		String logfile="/opt/test.log";
		LogFileTailer tailer=new LogFileTailer(10000, new File(logfile));
		tailer.addLogFileTailerListener(this);
		this.t = new Thread(tailer);
		t.start();
	}

	public void newLogFileLine(String line) {
		// TODO Auto-generated method stub
		System.out.println("Line in client::"+line);
	}

	public void close() {
		// TODO Auto-generated method stub
		
		
	}
}
