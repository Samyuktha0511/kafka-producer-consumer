package com.kfkProdCons.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class kfkStreamProcessor {
    public static void processStream() {
        Properties props = new Properties();

        try (InputStream input = kfkConsumer.class.getClassLoader().getResourceAsStream("spConfig.properties")) {
            if (input == null) {
                System.err.println("❌ config.properties not found!");
                return;
            }
            props.load(input);
        } catch (Exception e) {
            System.err.println("❌ Error loading config.properties: " + e.getMessage());
            return;
        }

        StreamsConfig streamsConfig = new StreamsConfig(props);

        StreamsBuilder builder = new StreamsBuilder();
        String inputTopic = props.getProperty("input.topic");
        String palindromeTopic = props.getProperty("output.topic.1");
        String transformedTopic = props.getProperty("output.topic.2");
        
        KStream<String, String> wordStream = builder.stream(inputTopic);

        KStream<String, String> transformedStream = processWords(wordStream);
        transformedStream.to(palindromeTopic, Produced.with(Serdes.String(), Serdes.String()));

        // KStream<String, String> transformedStream_2 = processWords(wordStream);
        // transformedStream_2.to(transformedTopic, Produced.with(Serdes.String(), Serdes.String()));

        KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfig);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    public static KStream<String, String> processWords(KStream<String, String> wordStream) {
        return wordStream
            .flatMapValues(value -> Arrays.asList(value.split(" ")))  // Split into words
            .filter((key, value) -> value.length() > 3)  // Filter words whose length is greater than 3
            .mapValues(value -> { 
                value = value.toLowerCase(); //Convert to Lowercase
                String reversed = new StringBuilder(value).reverse().toString(); //Reverse string
                boolean isPalindrome = value.equalsIgnoreCase(reversed); //compare original value and reversed value
                return value + " (Palindrome: " + isPalindrome + ")";
            });
    }
    
    //split the sentence into words, 
    //filter the only words that start with "a", 
    //output only the length of the words
    // public static KStream<String, Long> processWords_2(KStream<String, String> wordStream) {
        
    // }
    

}
