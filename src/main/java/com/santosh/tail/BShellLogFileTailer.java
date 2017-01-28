package com.santosh.tail;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BShellLogFileTailer implements Runnable{

	//interval
	private long sampleInterval=2000;
	//Listeners
	@SuppressWarnings("rawtypes")
	private Set listeners=new HashSet();
	//log file tail
	private File logFile;
	private boolean tailing = false;
	
	
	public BShellLogFileTailer(long sampleInterval, File logFile) {
		super();
		this.sampleInterval = sampleInterval;
		this.logFile = logFile;
	}
	
	@SuppressWarnings("unchecked")
	public void addLogFileTailerListener(LogFileTailerListener logFileListener) {
		this.listeners.add(logFileListener);
	}

	public void removeLogFileTailerListener(LogFileTailerListener logFileListener) {
		this.listeners.remove(logFileListener);
	}
	
	@SuppressWarnings("rawtypes")
	protected void fireNewLogFileLine(String line) {
		for (Iterator i = this.listeners.iterator(); i.hasNext();) {
			LogFileTailerListener l = (LogFileTailerListener) i.next();
			l.newLogFileLine(line);
		}
	}

	public void stopTailing() {
		this.tailing = false;
	}

	@SuppressWarnings("rawtypes")
	private void close() {
		for (Iterator i = this.listeners.iterator(); i.hasNext();) {
			LogFileTailerListener l = (LogFileTailerListener) i.next();
			l.close();
		}
	}
	
	

	public void run() {
		long filePointer = 0;

		
			filePointer = this.logFile.length();
		    //System.out.println("filePointer::"+filePointer);

		try {
			this.tailing = true;
			RandomAccessFile file = new RandomAccessFile(logFile, "r");
			while (this.tailing) {
				long fileLength = this.logFile.length();
				//System.out.println("fileLength::"+fileLength);
				if (fileLength < filePointer) {
					file = new RandomAccessFile(logFile, "r");
					filePointer = 0;
				}
				if (fileLength > filePointer) {
					file.seek(filePointer);
					String line = null;
					String newLine="";
					String splitLine=null;
					boolean flag=false;
					
					//System.out.println("file.readLine()::"+file.readLine());
					while (null != (line = file.readLine())) {
						if(line.indexOf("******* S T A R T of")!=-1 && !flag){
							flag=true;
							newLine=line.substring(line.indexOf("of")+2,line.lastIndexOf("message *******")); // Error Types
							splitLine=line.substring(line.indexOf(":"),line.lastIndexOf(":"));; // Extract timestamp and user
							String[] tmpArray=splitLine.split(":");
							splitLine=tmpArray[5]; // Baan User
							newLine=newLine+"|"+splitLine+"|";
							newLine=newLine+line.substring(0,line.indexOf("*******"));
						}
						else if(flag && line.indexOf("******* S T A R T of")==-1 && line.indexOf("******* E N D of")==-1){
							newLine=newLine+line.substring(line.lastIndexOf(splitLine)+splitLine.length()+1,line.length());
						}if(line.indexOf("******* E N D of")!=-1){
							System.out.println(newLine);
							this.fireNewLogFileLine(newLine);
							flag=false;
						}

					}
					//System.out.println("line::"+line);
					filePointer = file.getFilePointer();
				}
				TimeUnit.MILLISECONDS.sleep(this.sampleInterval);
				

				long lastModify = this.logFile.lastModified();
				long curtime = System.currentTimeMillis();
				long modifyInterval = 3600000L;
				if (lastModify > 0 && curtime - lastModify >= modifyInterval) {
					System.out.println("Tail -f ended:" + this.logFile + ", with current time=" + curtime
							+ ", file modify time=" + lastModify);
					break;
				}
			}
			file.close();
			this.close();
		} catch (IOException exp) {
			exp.printStackTrace();
		} catch (InterruptedException iexp) {
			iexp.printStackTrace();
		}

	}
	
}
