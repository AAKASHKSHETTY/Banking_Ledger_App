package dev.codescreen.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class AuthResponse {
    private String userId;
    private String messageId;
    private ResponseCode responseCode;
    private Amount transactionAmount;
    public enum ResponseCode{
        APPROVED,DECLINE;
    }
}
