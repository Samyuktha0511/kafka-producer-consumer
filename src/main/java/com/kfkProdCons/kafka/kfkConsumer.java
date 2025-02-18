package com.kfkProdCons.kafka;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;
import java.time.Duration;


public class kfkConsumer {
    public static void consumeOnce() {
        Properties props = new Properties();

        try (InputStream input = kfkConsumer.class.getClassLoader().getResourceAsStream("consumerConfig.properties")) {
            if (input == null) {
                System.err.println("❌ config.properties not found!");
                return;
            }
            props.load(input);
        } catch (Exception e) {
            System.err.println("❌ Error loading config.properties: " + e.getMessage());
            return;
        }

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(props.getProperty("topic.name")));

            //Poll for messages (wait up to 5 seconds)
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(30));
            System.out.println("Number of records received: " + records.count());


            for (ConsumerRecord<String, String> record : records) {
                System.out.println("✅ Received message- "+ record.key()+ ": "+ record.value());
            }

            if (records.isEmpty()) {
                System.out.println("No more messages found. Exiting...");
            }

        } catch (Exception e) {
            System.err.println("❌ Error in consumer: " + e.getMessage());
        }


    }
}
