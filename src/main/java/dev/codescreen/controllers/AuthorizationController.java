package dev.codescreen.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dev.codescreen.models.*;
import dev.codescreen.services.Ledger;

@RestController
public class AuthorizationController {
    @Autowired
    Ledger ledger;

    // See balances of all the users in the system.
    @GetMapping("/users")
    public Map<String, Amount> seeUsers() throws Exception 
    {
        return ledger.UsersList();
    }

    // Ping the server to check if the system is up and running.
    @GetMapping("/ping")
    public String ping() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        
        // Format date-time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        return "Server time: " + formattedDateTime;
    }

    // authorization to debit from the bank account.
    @PutMapping("/authorization")
    public AuthResponse authorizations(@RequestBody Authorization AuthInfo) throws Exception 
    {
        return ledger.authorizations(AuthInfo);
    }

    // load to credit to the bank account.
    @PutMapping("/load")
    public LoadResponse loads(@RequestBody Load LoadInfo) throws Exception 
    {
        return ledger.loads(LoadInfo);
    }

    // see the event logs for all past transactions.
    @GetMapping("/logs")
    public ArrayList<TransactionEvent> viewLog() throws Exception 
    {
        return ledger.viewLog();
    }    
}
