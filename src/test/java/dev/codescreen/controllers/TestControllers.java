package dev.codescreen.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import dev.codescreen.models.Amount;
import dev.codescreen.models.AuthResponse;
import dev.codescreen.models.Authorization;
import dev.codescreen.models.Load;
import dev.codescreen.models.LoadResponse;
import dev.codescreen.models.AuthResponse.ResponseCode;
import dev.codescreen.services.Ledger;

@ExtendWith(MockitoExtension.class)
public class TestControllers {
    @InjectMocks
    AuthorizationController authorizationController;

    @Mock
    Ledger ledger;

    @Test
    public void TestAuthorizationApi() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Amount TestTransactionAmount1 = Amount.builder()
        .amount("10.00")
        .currency("USD")
        .debitOrCredit(Amount.DebitCredit.DEBIT)
        .build();

        Authorization TestAuthInfo = Authorization.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(TestTransactionAmount1)
        .build();
        Amount TestTransactionAmount2 = Amount.builder()
        .amount("0.00")
        .currency("USD")
        .debitOrCredit(Amount.DebitCredit.DEBIT)
        .build();
        AuthResponse TestAuthResp = AuthResponse.builder()
        .userId("1")
        .messageId("1")
        .responseCode(ResponseCode.DECLINED)
        .transactionAmount(TestTransactionAmount2)
        .build();

        when(ledger.authorizations(any(Authorization.class))).thenReturn(TestAuthResp);

        AuthResponse TestAuthRespInfo = authorizationController.authorizations(TestAuthInfo);

        Assertions.assertThat(TestAuthRespInfo.getResponseCode()).isEqualTo(TestAuthResp.getResponseCode());
    }
    @Test
    public void TestLoadApi() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Amount TestTransactionAmount1 = Amount.builder()
        .amount("10.00")
        .currency("USD")
        .debitOrCredit(Amount.DebitCredit.DEBIT)
        .build();

        Load TestLoadInfo = Load.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(TestTransactionAmount1)
        .build();

        LoadResponse TestLoadResp = LoadResponse.builder()
        .userId("1")
        .messageId("1")
        .transactionAmount(TestTransactionAmount1)
        .build();
        when(ledger.loads(any(Load.class))).thenReturn(TestLoadResp);

        LoadResponse TestLoadRespInfo = authorizationController.loads(TestLoadInfo);

        Assertions.assertThat(TestLoadRespInfo.getTransactionAmount().getAmount()).isEqualTo(TestLoadInfo.getTransactionAmount().getAmount());
    }





}
