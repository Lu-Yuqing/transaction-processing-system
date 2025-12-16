package com.jpmc.midascore;

import com.jpmc.midascore.producer.KafkaProducer;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.util.FileLoader;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;
import java.util.stream.StreamSupport;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class IncentiveDebugTest {
    static final Logger logger = LoggerFactory.getLogger(IncentiveDebugTest.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;
    
    @Autowired
    private UserRepository userRepository;

    @Test
    void debugIncentiveIntegration() throws InterruptedException {
        userPopulator.populate();
        
        // Wait for consumer to be ready
        Thread.sleep(2000);
        
        // Process just the first few transactions to debug
        String[] transactionLines = fileLoader.loadStrings("/test_data/alskdjfh.fhdjsk");
        for (int i = 0; i < 3; i++) { // Only process first 3 transactions
            String transactionLine = transactionLines[i];
            logger.info("Sending transaction line: {}", transactionLine);
            kafkaProducer.send(transactionLine);
        }
        
        Thread.sleep(5000); // Wait longer for transactions to be processed

        // Check wilbur's balance
        Optional<com.jpmc.midascore.entity.UserRecord> wilburOpt = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(user -> "wilbur".equals(user.getName()))
                .findFirst();
        
        if (wilburOpt.isPresent()) {
            float wilburBalance = wilburOpt.get().getBalance();
            logger.info("Wilbur's balance after first 3 transactions: {}", wilburBalance);
        } else {
            logger.error("Wilbur not found!");
        }
        
        // Check antonio's balance (should have received incentive)
        Optional<com.jpmc.midascore.entity.UserRecord> antonioOpt = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(user -> "antonio".equals(user.getName()))
                .findFirst();
        
        if (antonioOpt.isPresent()) {
            float antonioBalance = antonioOpt.get().getBalance();
            logger.info("Antonio's balance after first 3 transactions: {}", antonioBalance);
        } else {
            logger.error("Antonio not found!");
        }
    }
}
