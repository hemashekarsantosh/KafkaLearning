package com.santosh.tail;

public interface LogFileTailerListener {

	public void newLogFileLine(String line);
	public void close();
}
