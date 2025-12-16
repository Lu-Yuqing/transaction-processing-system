package com.jpmc.midascore.producer;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducer {
    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    
    @Value("${general.kafka-topic}")
    private String topic;

    public KafkaProducer(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String transactionLine) {
        System.out.println("KafkaProducer: Sending transaction line: " + transactionLine);
        String[] parts = transactionLine.split(", ");
        if (parts.length == 3) {
            long senderId = Long.parseLong(parts[0]);
            long recipientId = Long.parseLong(parts[1]);
            float amount = Float.parseFloat(parts[2]);
            
            Transaction transaction = new Transaction(senderId, recipientId, amount);
            System.out.println("KafkaProducer: Created transaction: " + transaction);
            kafkaTemplate.send(topic, transaction);
            System.out.println("KafkaProducer: Sent transaction to topic: " + topic);
        } else {
            System.out.println("KafkaProducer: Invalid transaction line format: " + transactionLine);
        }
    }
}
