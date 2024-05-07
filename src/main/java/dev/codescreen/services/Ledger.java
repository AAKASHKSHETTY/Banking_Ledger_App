package dev.codescreen.services;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import dev.codescreen.models.*;
import dev.codescreen.models.Amount.DebitCredit;
import dev.codescreen.exceptions.*;
import dev.codescreen.models.AuthResponse.ResponseCode;
import dev.codescreen.models.TransactionEvent.Action;

@Service
public class Ledger{
    // Hash Map for balances to keep track of the user amounts.
    private HashMap<String, Amount> balances;

    // Event log to keep track of all transactions taken place in the system.
    private ArrayList<TransactionEvent> eventlog;

    public Ledger(HashMap<String, Amount> balances, ArrayList<TransactionEvent> eventlog){
        this.balances = balances;
        this.eventlog = eventlog;
    }

    // return balances for all users, for /user path.
    public String UsersList(String u_Id) throws InvalidUserException{
        if (balances.containsKey(u_Id)){
            return balances.get(u_Id).getAmount() + balances.get(u_Id).getCurrency() ;
        }
        else throw new InvalidUserException("User not Present in the System.");
    }

    // return eventlog list for /logs path.
    // public ArrayList<TransactionEvent> viewLog(){
    //     return eventlog;
    // }

    // function to store the transactions into the eventlogs.
    public String EventLog(String userId, String amount,DebitCredit debitorcredit,String currency, Action action){
        TransactionEvent event = new TransactionEvent(userId, amount,debitorcredit, currency, System.currentTimeMillis(), action);
        eventlog.add(event);
        return "Success";
    }
    
    // logic for /authorization path, check for exception and calculation of debits.
    public AuthResponse authorizations(Authorization AuthInfo) throws InvalidUserException,DebitCreditException,CurrencyException{
        String userId = AuthInfo.getUserId();
        AuthResponse result = new AuthResponse(); // return result as AuthResponse after updating the balances.
        result.setUserId(userId);
        result.setMessageId(AuthInfo.getMessageId());

        // Check DEBIT for authorizations.
        if (AuthInfo.getTransactionAmount().getDebitOrCredit() != DebitCredit.DEBIT) throw new DebitCreditException("Authorization can only perform DEBIT actions.");

        // If userId is present in balances we can proceed with updating the balances, else return DECLINED Response.
        if (balances.containsKey(userId)) {

            // Check if authorizations are below available balance amount else return DECLINED Response.
            if (Float.parseFloat(balances.get(userId).getAmount()) >= Float.parseFloat(AuthInfo.getTransactionAmount().getAmount()))
            {
                // Check for currency mismatch to our intial load.
                if (!AuthInfo.getTransactionAmount().getCurrency().equals(balances.get(userId).getCurrency())) throw new CurrencyException("There is a currency mismatch, you can only withdraw currency you already have.");

                // Update the balance for the particular user and limit value up to 2 decimal places.
                Float value = Float.parseFloat(balances.get(userId).getAmount()) - Float.parseFloat(AuthInfo.getTransactionAmount().getAmount());
                String str_value = String.format("%.2f", value);
                balances.get(userId).setAmount(str_value);
                balances.get(userId).setDebitOrCredit(AuthInfo.getTransactionAmount().getDebitOrCredit());
                result.setResponseCode(ResponseCode.APPROVED);
                result.setTransactionAmount(balances.get(userId));

                // Save to event logs and return result after update.
                EventLog(userId,AuthInfo.getTransactionAmount().getAmount(),AuthInfo.getTransactionAmount().getDebitOrCredit(),AuthInfo.getTransactionAmount().getCurrency(),Action.APPROVED);
                return result;
            }
            // DECLINED Responses for InvalidUser and Insufficient Funds.
            else
            {
                result.setResponseCode(ResponseCode.DECLINED);
                result.setTransactionAmount(balances.get(userId));
                EventLog(userId,AuthInfo.getTransactionAmount().getAmount(),AuthInfo.getTransactionAmount().getDebitOrCredit(),AuthInfo.getTransactionAmount().getCurrency(),Action.DECLINED);
                return result;
            }
        }
        result.setResponseCode(ResponseCode.DECLINED);
        Amount new_Amount = AuthInfo.getTransactionAmount();
        new_Amount.setAmount("0.00");
        result.setTransactionAmount(new_Amount);
        return result;
    }

    // logic for /load path, calculation of loads.
    public LoadResponse loads(Load LoadInfo) throws DebitCreditException,CurrencyException {
        String userId = LoadInfo.getUserId();
        LoadResponse result = new LoadResponse(); // return result as AuthResponse after updating the balances.

        // Check CREDIT for authorizations.
        if (LoadInfo.getTransactionAmount().getDebitOrCredit() != DebitCredit.CREDIT) throw new DebitCreditException("Load can only perform CREDIT actions.");

        // Save load request to eventLog.
        EventLog(userId,LoadInfo.getTransactionAmount().getAmount(),LoadInfo.getTransactionAmount().getDebitOrCredit(),LoadInfo.getTransactionAmount().getCurrency(),Action.APPROVED);

        // If userId is present in balances we can proceed with updating the balances, else create new userId.
        if (balances.containsKey(userId)) {

            // Check for currency mismatch to our intial load.
            if (!LoadInfo.getTransactionAmount().getCurrency().equals(balances.get(userId).getCurrency())) throw new CurrencyException("There is a currency mismatch, you can only add currency you already have.");

            // Update balances.
            Float value = Float.parseFloat(balances.get(userId).getAmount()) + Float.parseFloat(LoadInfo.getTransactionAmount().getAmount());
            String str_value = String.format("%.2f", value);
            balances.get(userId).setAmount(str_value);
            balances.get(userId).setDebitOrCredit(LoadInfo.getTransactionAmount().getDebitOrCredit());
        }
        // Create new user, if not already in our system.
        else
        {
            Float value = Float.parseFloat(LoadInfo.getTransactionAmount().getAmount());
            String str_value = String.format("%.2f", value);
            Amount new_Amount = new Amount(str_value, LoadInfo.getTransactionAmount().getCurrency(),LoadInfo.getTransactionAmount().getDebitOrCredit());
            balances.put(userId,new_Amount);
        }

        // return result about the updated balances.
        result.setUserId(userId);
        result.setMessageId(LoadInfo.getMessageId());
        result.setTransactionAmount(balances.get(userId));
        return result;
    }
}
