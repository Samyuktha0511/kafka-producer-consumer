package com.kfkProdCons.kafka;

import org.apache.kafka.clients.producer.*;
import java.io.InputStream;
import java.util.*;

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
        String key = "my-name";  
        String message = "Naa dhaan da Leo";

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {

            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.println("✅ Sent to " + metadata.topic() + " | Partition: " + metadata.partition());
                } else {
                    System.err.println("❌ Error sending message: " + exception.getMessage());
                }
            });

            producer.close();
        } catch (Exception e) {
            System.err.println("❌ Error in Kafka producer: " + e.getMessage());
        }

        
        System.out.println("Producer closed.");
    }
}