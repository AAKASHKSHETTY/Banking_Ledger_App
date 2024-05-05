package dev.codescreen.models;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class TransactionEvent {
    private String UserId;
    private Amount balance;
    private long timestamp;
    private Action action;

    public enum Action{
        APPROVED,DECLINED
    }
}
