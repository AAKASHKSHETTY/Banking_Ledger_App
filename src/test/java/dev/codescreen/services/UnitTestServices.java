package dev.codescreen.services;

import java.util.ArrayList;
import java.util.HashMap;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import dev.codescreen.models.*;
import dev.codescreen.models.AuthResponse.ResponseCode;

@SpringBootTest
public class UnitTestServices {

    Ledger ledger;
    
    @BeforeEach
    void setUp() {
        // @Autowired
        ledger = new Ledger(new HashMap<>(), new ArrayList<>());
    }

    // Build Authorization Unit Test for DECLINED.
    @Test
    public void TestAuthorizationsDecline() throws Exception{

        Amount TestTransactionAmount = Amount.builder()
        .amount("10.00")
        .currency("USD")
        .debitOrCredit(Amount.DebitCredit.DEBIT)
        .build();

        Authorization TestAuthInfo = Authorization.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(TestTransactionAmount)
        .build();

        AuthResponse TestAuthInfoResp = ledger.authorizations(TestAuthInfo);

        Assertions.assertThat(TestAuthInfoResp).isNotNull();
        Assertions.assertThat(TestAuthInfoResp.getResponseCode()).isEqualTo(ResponseCode.DECLINED);
    }

    // Build Authorization Unit Test for APPROVED.
    @Test
    public void TestAuthorizationsApprove() throws Exception{

        Amount TestTransactionAmount = Amount.builder()
        .amount("10.00")
        .currency("USD")
        .debitOrCredit(Amount.DebitCredit.DEBIT)
        .build();

        Authorization TestAuthInfo = Authorization.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(TestTransactionAmount)
        .build();

        HashMap<String, HashMap<String, Amount>> balances1 = new HashMap<>();
        HashMap<String, Amount> innerMap = new HashMap<>(){{put("USD",TestTransactionAmount);}};
        balances1.put("1",innerMap);
        
        Ledger ledger1 = new Ledger(balances1,new ArrayList<>());

        AuthResponse TestAuthInfoResp1 = ledger1.authorizations(TestAuthInfo);
        Assertions.assertThat(TestAuthInfoResp1).isNotNull();
        Assertions.assertThat(TestAuthInfoResp1.getResponseCode()).isEqualTo(ResponseCode.APPROVED);
        Assertions.assertThat(TestAuthInfoResp1.getTransactionAmount().getAmount()).isEqualTo("0.00");
    }
   
    // Build Load Unit Test for New User.
    @Test
    public void TestLoadsNewUser() throws Exception{
        
        Amount TestLoadAmount = Amount.builder()
        .amount("200.00")
        .currency("USD")
        .debitOrCredit(Amount.DebitCredit.CREDIT)
        .build();
        
        Load TestLoadInfo = Load.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(TestLoadAmount)
        .build();

        LoadResponse TestLoadInfoResp = ledger.loads(TestLoadInfo);

        Assertions.assertThat(TestLoadInfoResp).isNotNull();
        Assertions.assertThat(TestLoadInfoResp.getTransactionAmount().getAmount()).isEqualTo("200.00");

        LoadResponse TestLoadInfoResp1 = ledger.loads(TestLoadInfo);
        Assertions.assertThat(TestLoadInfoResp1.getTransactionAmount().getAmount()).isEqualTo("400.00");
    }

    // Build Load Unit Test for Existing User.
    @Test
    public void TestLoadsExistingUser() throws Exception{
        
        Amount TestLoadAmount = Amount.builder()
        .amount("200.00")
        .currency("USD")
        .debitOrCredit(Amount.DebitCredit.CREDIT)
        .build();
        
        Load TestLoadInfo = Load.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(TestLoadAmount)
        .build();
        HashMap<String, HashMap<String, Amount>> balances1 = new HashMap<>();
        HashMap<String, Amount> innerMap = new HashMap<>(){{put("USD",TestLoadAmount);}};
        balances1.put("1",innerMap);
        
        Ledger ledger1 = new Ledger(balances1,new ArrayList<>());

        LoadResponse TestLoadInfoResp1 = ledger1.loads(TestLoadInfo);
        Assertions.assertThat(TestLoadInfoResp1.getTransactionAmount().getAmount()).isEqualTo("400.00");
    }

    
}
