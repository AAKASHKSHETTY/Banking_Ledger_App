package dev.codescreen.models;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class Load {
    private String userId;
    private String messageId;
    private Amount transactionAmount;
}
