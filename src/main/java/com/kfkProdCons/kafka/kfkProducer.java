package com.kfkProdCons.kafka;

import org.apache.kafka.clients.producer.*;
import java.io.InputStream;
import java.util.Properties;

public class kfkProducer {
    public static void produceOnce() {
        Properties props = new Properties();

        try (InputStream input = kfkProducer.class.getClassLoader().getResourceAsStream("producerConfig.properties")) {
            if (input == null) {
                System.err.println("❌ config.properties not found!");
                return;
            }
            props.load(input);
        } catch (Exception e) {
            System.err.println("❌ Error loading config.properties: " + e.getMessage());
            return;
        }

        String topic = props.getProperty("topic");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            String message = "sangi mangi.. adangooo";

            ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.println("✅ Sent to " + metadata.topic() + " | Partition: " + metadata.partition());
                    producer.close();
                } else {
                    System.err.println("❌ Error sending message: " + exception.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("❌ Error in Kafka producer: " + e.getMessage());
        }

        
        System.out.println("Producer closed.");
    }
}