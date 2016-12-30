package com.santosh.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;



public class SimpleProducer {

	public static void main(String[] args) {
		//kafka topic name
		String topicName="testing";
		//message to e published
		String message="Kafka java Client3";
		//configure the producer
		Properties configProperties= new Properties();
		configProperties.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
		configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		configProperties.put(ProducerConfig.ACKS_CONFIG,"1");
		// create producer client
		Producer<String, String> producer=new KafkaProducer<String, String>(configProperties);
		//prepare message
		ProducerRecord<String, String> rec=new ProducerRecord<String, String>(topicName, message);
		//send to kafka
		producer.send(rec);
		//close the producer
		producer.close();
		

	}

}
