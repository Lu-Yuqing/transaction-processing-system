package com.jpmc.midascore.service;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final IncentiveService incentiveService;
    
    public TransactionService(UserRepository userRepository, TransactionRecordRepository transactionRecordRepository, IncentiveService incentiveService) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.incentiveService = incentiveService;
    }
    
    @Transactional
    public boolean processTransaction(Transaction transaction) {
        logger.info("Processing transaction: {}", transaction);
        
        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
        if (senderOpt.isEmpty()) {
            logger.warn("Transaction rejected: Sender with ID {} not found", transaction.getSenderId());
            return false;
        }
        
        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());
        if (recipientOpt.isEmpty()) {
            logger.warn("Transaction rejected: Recipient with ID {} not found", transaction.getRecipientId());
            return false;
        }
        
        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();
        
        if (sender.getBalance() < transaction.getAmount()) {
            logger.warn("Transaction rejected: Sender {} has insufficient balance. Required: {}, Available: {}", 
                       sender.getName(), transaction.getAmount(), sender.getBalance());
            return false;
        }
        
        try {
            Incentive incentive = incentiveService.getIncentive(transaction);
            float incentiveAmount = incentive.getAmount();
            
            sender.setBalance(sender.getBalance() - transaction.getAmount());
            recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount);
            
            userRepository.save(sender);
            userRepository.save(recipient);
            
            TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
            transactionRecordRepository.save(transactionRecord);
            
            logger.info("Transaction processed successfully: {} sent {} to {} with incentive {}", 
                       sender.getName(), transaction.getAmount(), recipient.getName(), incentiveAmount);
            return true;
            
        } catch (Exception e) {
            logger.error("Error processing transaction: {}", e.getMessage(), e);
            return false;
        }
    }
}

