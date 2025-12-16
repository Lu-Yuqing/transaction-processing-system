package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class BalanceController {
    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);

    private final UserRepository userRepository;

    @Autowired
    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") Long userId) {
        logger.info("Balance query requested for userId: {}", userId);
        
        Optional<com.jpmc.midascore.entity.UserRecord> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            float balance = userOpt.get().getBalance();
            logger.info("Found user with balance: {}", balance);
            return new Balance(balance);
        } else {
            logger.info("User not found, returning balance 0");
            return new Balance(0.0f);
        }
    }
}

