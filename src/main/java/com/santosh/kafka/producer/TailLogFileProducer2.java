package com.santosh.kafka.producer;

import java.io.File;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.santosh.tail.LogFileTailer;
import com.santosh.tail.LogFileTailerListener;

public class TailLogFileProducer2 implements LogFileTailerListener{
	
	private File logFile;
	private long sampleInterval;
	private String topicName;
	private Producer<String, String> producer;
	private Thread tailLogfileThread;
	private LogFileTailer tailer;
	
	
	
	public File getLogFile() {
		return logFile;
	}


	public void setLogFile(File logFile) {
		this.logFile = logFile;
	}


	public long getSampleInterval() {
		return sampleInterval;
	}


	public void setSampleInterval(long sampleInterval) {
		this.sampleInterval = sampleInterval;
	}


	public String getTopicName() {
		return topicName;
	}


	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}


	public Producer<String, String> getProducer() {
		return producer;
	}


	public void setProducer(Producer<String, String> producer) {
		this.producer = producer;
	}


	public Thread getTailLogfileThread() {
		return tailLogfileThread;
	}


	public void setTailLogfileThread(Thread tailLogfileThread) {
		this.tailLogfileThread = tailLogfileThread;
	}


	public LogFileTailer getTailer() {
		return tailer;
	}


	public void setTailer(LogFileTailer tailer) {
		this.tailer = tailer;
	}


	public void startTailing(File logFile, long sampleInterval, String topicName) {
		
		this.logFile = logFile;
		this.sampleInterval = sampleInterval;
		this.topicName = topicName;
		Properties configProperties= new Properties();
		configProperties.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
		configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		this.producer=new KafkaProducer<String, String>(configProperties);
		tailer=new LogFileTailer(sampleInterval, logFile);
		tailer.addLogFileTailerListener(this);
		this.tailLogfileThread=new Thread(tailer);
		this.tailLogfileThread.start();
	}


	public void newLogFileLine(String line) {
		ProducerRecord<String, String> data = new ProducerRecord<String, String>(
				this.topicName, line);
		this.producer.send(data);
		
	}

	public void close() {
		this.producer.close();
		
	}
	
	public static void main(String[] args) {
		//String logFile1="D:/srtwebaccess.log";
		String logFile2="D:/ebpmaccess.log";
		String topicName="srtaccesslog";
		long sampleInterval=2000;
		TailLogFileProducer2 producer=new TailLogFileProducer2();
		//producer.startTailing(new File(logFile1), sampleInterval, topicName);
		producer.startTailing(new File(logFile2), sampleInterval, topicName);
		
		

	}

}
