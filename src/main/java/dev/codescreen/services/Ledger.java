package dev.codescreen.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import dev.codescreen.models.*;
import dev.codescreen.models.Amount.DebitCredit;
import dev.codescreen.exceptions.*;
import dev.codescreen.models.AuthResponse.ResponseCode;
import dev.codescreen.models.TransactionEvent.Action;

@Service
public class Ledger{
    // Hash Map for balances to keep track of the user amounts.
    Map<String, Amount> balances = new HashMap<>();

    // Event log for all transactions taken place in the system. 
    ArrayList<TransactionEvent> eventlog = new ArrayList<TransactionEvent>();

    // return balances for all users, for /user path.
    public Map<String, Amount> UsersList(){
        return balances;
    }

    // return eventlog list for /logs path.
    public ArrayList<TransactionEvent> viewLog(){
        return eventlog;
    }

    // function to store the transactions into the eventlogs.
    public void EventLog(String userId, Amount amount, Action action){
        TransactionEvent event = new TransactionEvent();
        event.setUserId(userId);
        event.setBalance(amount);
        event.setAction(action);
        event.setTimestamp(System.currentTimeMillis());
        eventlog.add(event);
    }
    
    // logic for /authorization path, check for exception and calculation of debits.
    public AuthResponse authorizations(Authorization AuthInfo) throws InsufficientFundsException,InvalidUserException,DebitCreditException,CurrencyException{
        String userId = AuthInfo.getUserId();
        AuthResponse result = new AuthResponse();
        
        if (AuthInfo.getTransactionAmount().getDebitOrCredit() != DebitCredit.DEBIT) throw new DebitCreditException("Authorization can only perform DEBIT actions.");
        if (balances.containsKey(userId)) {
            if (Float.parseFloat(balances.get(userId).getAmount()) >= Float.parseFloat(AuthInfo.getTransactionAmount().getAmount()))
            {
                if (!AuthInfo.getTransactionAmount().getCurrency().equals(balances.get(userId).getCurrency())) throw new CurrencyException("There is a currency mismatch, you can only withdraw currency you already have.");
                Float value = Float.parseFloat(balances.get(userId).getAmount()) - Float.parseFloat(AuthInfo.getTransactionAmount().getAmount());
                balances.get(userId).setAmount(String.valueOf(value));
                balances.get(userId).setDebitOrCredit(AuthInfo.getTransactionAmount().getDebitOrCredit());
                result.setUserId(userId);
                result.setMessageId(AuthInfo.getMessageId());
                result.setResponseCode(ResponseCode.APPROVED);
                result.setTransactionAmount(balances.get(userId));
                EventLog(userId, AuthInfo.getTransactionAmount(),Action.APPROVED);
                return result;
            }
            else
            {
                EventLog(userId, AuthInfo.getTransactionAmount(),Action.DECLINED);
                throw new InsufficientFundsException("You do not have enough funds to withdraw.");
            }
        }
        else throw new InvalidUserException("No such user present in our system.");
    }

    // logic for /load path, calculation of loads.
    public LoadResponse loads(Load LoadInfo) throws DebitCreditException,CurrencyException {
        String userId = LoadInfo.getUserId();
        LoadResponse result = new LoadResponse();
        EventLog(userId, LoadInfo.getTransactionAmount(),Action.APPROVED);
        if (LoadInfo.getTransactionAmount().getDebitOrCredit() != DebitCredit.CREDIT) throw new DebitCreditException("Load can only perform CREDIT actions.");
        if (balances.containsKey(userId)) {
            System.err.println(""+LoadInfo.getTransactionAmount().getCurrency()+" , "+balances.get(userId).getCurrency());
            if (!LoadInfo.getTransactionAmount().getCurrency().equals(balances.get(userId).getCurrency())) throw new CurrencyException("There is a currency mismatch, you can only add currency you already have.");
            Float value = Float.parseFloat(balances.get(userId).getAmount()) + Float.parseFloat(LoadInfo.getTransactionAmount().getAmount());
            balances.get(userId).setAmount(String.valueOf(value));
            balances.get(userId).setDebitOrCredit(LoadInfo.getTransactionAmount().getDebitOrCredit());
        }
        else
        {
            balances.put(userId,LoadInfo.getTransactionAmount());
        }
        result.setUserId(userId);
        result.setMessageId(LoadInfo.getMessageId());
        result.setTransactionAmount(balances.get(userId));
        return result;
    }
}
