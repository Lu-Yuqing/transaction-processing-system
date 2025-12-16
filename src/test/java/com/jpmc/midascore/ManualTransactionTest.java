package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.service.TransactionService;
import com.jpmc.midascore.util.FileLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class ManualTransactionTest {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private FileLoader fileLoader;
    
    @Test
    public void calculateWaldorfBalance() {
        // Load user data
        String[] userLines = fileLoader.loadStrings("/test_data/lkjhgfdsa.hjkl");
        Map<Long, UserRecord> users = new HashMap<>();
        
        for (int i = 0; i < userLines.length; i++) {
            String userLine = userLines[i];
            String[] userData = userLine.split(", ");
            UserRecord user = new UserRecord(userData[0], Float.parseFloat(userData[1]));
            user.setId(i + 1);
            users.put(user.getId(), user);
            System.out.println("User " + user.getName() + " (ID: " + user.getId() + ") has balance: " + user.getBalance());
        }
        
        // Load and process transactions
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");
        System.out.println("\nProcessing " + transactionLines.length + " transactions:");
        
        for (String transactionLine : transactionLines) {
            String[] parts = transactionLine.split(", ");
            long senderId = Long.parseLong(parts[0]);
            long recipientId = Long.parseLong(parts[1]);
            float amount = Float.parseFloat(parts[2]);
            
            UserRecord sender = users.get(senderId);
            UserRecord recipient = users.get(recipientId);
            
            if (sender != null && recipient != null && sender.getBalance() >= amount) {
                // Process transaction
                sender.setBalance(sender.getBalance() - amount);
                recipient.setBalance(recipient.getBalance() + amount);
                
                System.out.println("Transaction: " + sender.getName() + " -> " + recipient.getName() + 
                                 " amount: " + amount + " (Sender balance: " + sender.getBalance() + 
                                 ", Recipient balance: " + recipient.getBalance() + ")");
            } else {
                System.out.println("REJECTED: " + transactionLine + " (Sender: " + 
                                 (sender != null ? sender.getName() : "null") + 
                                 ", Recipient: " + (recipient != null ? recipient.getName() : "null") + 
                                 ", Sender balance: " + (sender != null ? sender.getBalance() : "N/A") + ")");
            }
        }
        
        // Find waldorf's final balance
        UserRecord waldorf = users.get(5L); // waldorf has ID 5
        if (waldorf != null) {
            System.out.println("\n=== FINAL RESULT ===");
            System.out.println("Waldorf's final balance: " + waldorf.getBalance());
            System.out.println("Waldorf's final balance (rounded down): " + (int) waldorf.getBalance());
        }
    }
}

