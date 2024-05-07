package dev.codescreen.models;
import dev.codescreen.models.Amount.DebitCredit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @AllArgsConstructor @Builder @ToString
public class TransactionEvent {
    private final String UserId;
    private final String amount;
    private final DebitCredit DebitOrCredit;
    private final String currency;
    private final long timestamp;
    private final Action action;

    public enum DebitorCredit{
        DEBIT,CREDIT
    }
    public enum Action{
        APPROVED,DECLINED
    }
}
