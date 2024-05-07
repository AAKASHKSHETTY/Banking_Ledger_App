package dev.codescreen.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class Amount {
    private String amount;
    private String currency;
    private DebitCredit debitOrCredit ;

    public enum DebitCredit{
        CREDIT, DEBIT;
    }
}
