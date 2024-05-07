package dev.codescreen.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class Authorization {
    private String userId;
    private String messageId;
    private Amount transactionAmount;
}
