package com.kfkProdCons.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class KfkStreamProcessor {
    public static void main(String[] args) {
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

        StreamsBuilder builder = new StreamsBuilder();
        String inputTopic = props.getProperty("input.topic");
        String outputTopic = props.getProperty("output.topic");
        
        KStream<String, String> wordStream = builder.stream(inputTopic);

        KStream<String, Long> transformedStream = processWords(wordStream);
        transformedStream.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));

        KStream<String, Long> transformedStream_2 = processWords(wordStream);
        transformedStream_2.toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), streamsConfig);
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    public static KStream<String, String> processWords(KStream<String, String> wordStream) {
        return wordStream
            .flatMapValues(value -> Arrays.asList(value.split(" ")))  // Split into words
            .filter((key, word) -> word.length() > 3)  // Filter words whose length is greater than 3
            .mapValues(String::toLowerCase) // Convert the words to lower case
            .mapValues(word -> {
                String reversed = new StringBuilder(word).reverse().toString();  // Reverse the word
                boolean isPalindrome = word.equalsIgnoreCase(reversed);  // Check if the word is a palindrome
                return word + " (Palindrome: " + isPalindrome + ")";  // Output the word and palindrome check
            });
    }
    
    //split the sentence into words, 
    //convert into uppercase, 
    //filter the only words that start with "a", 
    //output only the lenght of the words
    public static KStream<String, Long> processWords(KStream<String, String> wordStream) {
        return wordStream
            .flatMapValues(value -> Arrays.asList(value.split(" ")))  // Split into words
            .filter((key, word) -> word.startsWith("a"))  // Filter words that do not start with "a"
            .mapValues(word -> (long) word.length());  // Output the length of the remaining words
    }
    

}
