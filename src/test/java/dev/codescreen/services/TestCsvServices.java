package dev.codescreen.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.codescreen.models.Amount;
import dev.codescreen.models.Amount.DebitCredit;
import dev.codescreen.models.AuthResponse;
import dev.codescreen.models.AuthResponse.ResponseCode;
import dev.codescreen.models.Authorization;
import dev.codescreen.models.Load;
import dev.codescreen.models.LoadResponse;



// Test cases taken for both authorizations and load together read from CSV.
@SpringBootTest
public class TestCsvServices {

    @Autowired
    public Ledger ledger;
    
    // Fetching CSV File for parameterized Tests, as to perform multiple both authorization and load on multiple test cases.
    @ParameterizedTest
    @CsvFileSource(resources = "/test1.csv", numLinesToSkip = 1)
    public void testMultipleStatements(String action, String msgId, String userId, String debitOrCredit, String amount, String expectedResponseCode, String expectedBalance) throws Exception {

        // If action is AUTHORIZATION Run tests for Authorization.
        if (action.equals("AUTHORIZATION")){
            
            // Create Authorization Object to populate the inputs.
            Amount TestTransactionAmount = Amount.builder()
            .amount(amount)
            .currency("USD")
            .debitOrCredit(DebitCredit.valueOf(debitOrCredit))
            .build();

            Authorization TestAuthInfo = Authorization.builder()
            .userId(userId)
            .messageId(msgId)
            .transactionAmount(TestTransactionAmount)
            .build();

            // Call authorizations to get the responses.
            AuthResponse TestAuthInfoResp = ledger.authorizations(TestAuthInfo);

            // Run Assertions to check if our responses match the expectedResponseCode and expectedbalance from CSV.
            Assertions.assertThat(TestAuthInfoResp).isNotNull();
            Assertions.assertThat(TestAuthInfoResp.getResponseCode()).isEqualTo(ResponseCode.valueOf((expectedResponseCode)));
            Assertions.assertThat(TestAuthInfoResp.getTransactionAmount().getAmount()).isEqualTo(expectedBalance);
        }
        // If action is LOAD Run tests for LOAD.
        else{

            // Create Load Object to populate the inputs.
            Amount TestLoadAmount = Amount.builder()
            .amount(amount)
            .currency("USD")
            .debitOrCredit(DebitCredit.valueOf(debitOrCredit))
            .build();
            
            Load TestLoadInfo = Load.builder()
            .userId(userId)
            .messageId(msgId)
            .transactionAmount(TestLoadAmount)
            .build();

        // Call loads to get the responses.
        LoadResponse TestLoadInfoResp = ledger.loads(TestLoadInfo);

        // Run Assertions to check if our responses match the expectedbalance from CSV, LoadResponse does not return ReponseCode.
        Assertions.assertThat(TestLoadInfoResp).isNotNull();
        Assertions.assertThat(TestLoadInfoResp.getTransactionAmount().getAmount()).isEqualTo(expectedBalance);
        }
    }
}
