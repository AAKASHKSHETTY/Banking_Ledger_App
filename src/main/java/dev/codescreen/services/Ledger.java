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
public class Ledger
{
    // Hash Map for balances to keep track of the user amounts.
    private HashMap<String, HashMap<String,Amount>> balances;

    // Event log to keep track of all transactions taken place in the system.
    private ArrayList<TransactionEvent> eventlog;

    // Constructor
    public Ledger(HashMap<String, HashMap<String,Amount>> balances, ArrayList<TransactionEvent> eventlog)
    {
        this.balances = balances;
        this.eventlog = eventlog;
    }

    // return balances for all users, for /user path.
    // public HashMap<String, HashMap<String,Amount>> UsersList()
    // {
    //     return balances;
    // }

    // return eventlog list for /logs path.
    // public ArrayList<TransactionEvent> viewLog(){
    //     return eventlog;
    // }

    // function to store the transactions into the eventlogs.
    public String EventLog(String userId, String amount,DebitCredit debitorcredit,String currency, Action action)
    {
        TransactionEvent event = new TransactionEvent(userId, amount,debitorcredit, currency, System.currentTimeMillis(), action);
        eventlog.add(event);
        return "Success";
    }
    
    // logic for /authorization path, check for exception and calculation of debits.
    public AuthResponse authorizations(Authorization AuthInfo) throws DebitCreditException, BadValuesException{
        String userId = AuthInfo.getUserId();
        String currecy = AuthInfo.getTransactionAmount().getCurrency();
        AuthResponse result = new AuthResponse(); // return result as AuthResponse after updating the balances.
        result.setUserId(userId);
        result.setMessageId(AuthInfo.getMessageId());

        // Check DEBIT for authorizations.
        if (AuthInfo.getTransactionAmount().getDebitOrCredit() != DebitCredit.DEBIT) throw new DebitCreditException("Authorization can only perform DEBIT actions.");

        // Check for bad values, cannot have negative values or 0.
        if (Float.parseFloat(AuthInfo.getTransactionAmount().getAmount()) <= 0) throw new BadValuesException("Cannot Debit Negative or 0 from balance."); 

        // If userId is present in balances we can proceed with updating the balances, else return DECLINED Response.
        if (balances.containsKey(userId)) 
        {
            // Check for currency availability in balances.
            if (balances.get(userId).containsKey(currecy))
            {
                // Check if authorizations are below available balance amount else return DECLINED Response.
                if (Float.parseFloat(balances.get(userId).get(currecy).getAmount()) >= Float.parseFloat(AuthInfo.getTransactionAmount().getAmount()))
                {
                    // Update the balance for the particular user and limit value up to 2 decimal places.
                    Float value = Float.parseFloat(balances.get(userId).get(currecy).getAmount()) - Float.parseFloat(AuthInfo.getTransactionAmount().getAmount());
                    String str_value = String.format("%.2f", value);
                    balances.get(userId).get(currecy).setAmount(str_value);
                    balances.get(userId).get(currecy).setDebitOrCredit(AuthInfo.getTransactionAmount().getDebitOrCredit());
                    result.setResponseCode(ResponseCode.APPROVED);
                    result.setTransactionAmount(balances.get(userId).get(currecy));

                    // Save to event logs and return result after update.
                    EventLog(userId,AuthInfo.getTransactionAmount().getAmount(),AuthInfo.getTransactionAmount().getDebitOrCredit(),AuthInfo.getTransactionAmount().getCurrency(),Action.APPROVED);
                    return result;
                }
                // DECLINED Responses for InvalidUser and Insufficient Funds.
                else
                {
                    result.setResponseCode(ResponseCode.DECLINED);
                    result.setTransactionAmount(balances.get(userId).get(currecy));
                    EventLog(userId,AuthInfo.getTransactionAmount().getAmount(),AuthInfo.getTransactionAmount().getDebitOrCredit(),AuthInfo.getTransactionAmount().getCurrency(),Action.DECLINED);
                    return result;
                }
            }
        }
        result.setResponseCode(ResponseCode.DECLINED);
        Amount new_Amount = AuthInfo.getTransactionAmount();
        new_Amount.setAmount("0.00");
        result.setTransactionAmount(new_Amount);
        return result;
    }

    // logic for /load path, calculation of loads.
    public LoadResponse loads(Load LoadInfo) throws DebitCreditException, BadValuesException {
        String userId = LoadInfo.getUserId();
        String currency = LoadInfo.getTransactionAmount().getCurrency();
        LoadResponse result = new LoadResponse(); // return result as AuthResponse after updating the balances.

        // Check CREDIT for authorizations.
        if (LoadInfo.getTransactionAmount().getDebitOrCredit() != DebitCredit.CREDIT) throw new DebitCreditException("Load can only perform CREDIT actions.");

        // Check for bad values, cannot have negative values or 0.
        if (Float.parseFloat(LoadInfo.getTransactionAmount().getAmount()) <= 0) throw new BadValuesException("Cannot Credit Negative or 0 to the balance."); 

        // Save load request to eventLog.
        EventLog(userId,LoadInfo.getTransactionAmount().getAmount(),LoadInfo.getTransactionAmount().getDebitOrCredit(),LoadInfo.getTransactionAmount().getCurrency(),Action.APPROVED);

        // If userId is present in balances we can proceed with updating the balances, else create new userId.
        if (balances.containsKey(userId)) 
        {
            HashMap<String, Amount> innerMap = balances.get(userId);

            // Check for currency availability in balances.
            if (innerMap.containsKey(currency)) 
            {
                // Update existing Amount object
                Amount existingAmount = innerMap.get(currency);
                float newValue = Float.parseFloat(existingAmount.getAmount()) + Float.parseFloat(LoadInfo.getTransactionAmount().getAmount());
                existingAmount.setAmount(String.format("%.2f", newValue));
                existingAmount.setDebitOrCredit(LoadInfo.getTransactionAmount().getDebitOrCredit());
            } 
            else 
            {
                // Add new currency to existing user.
                Float value = Float.parseFloat(LoadInfo.getTransactionAmount().getAmount());
                String str_value = String.format("%.2f", value);
                Amount newAmount = new Amount(str_value, LoadInfo.getTransactionAmount().getCurrency(), LoadInfo.getTransactionAmount().getDebitOrCredit());
                innerMap.put(currency, newAmount);
            }
        }
        // Create new user, if not already in our system.
        else
        {
            Float value2 = Float.parseFloat(LoadInfo.getTransactionAmount().getAmount());
            String str_value2 = String.format("%.2f", value2);
            Amount new_Amount2 = new Amount(str_value2, LoadInfo.getTransactionAmount().getCurrency(),LoadInfo.getTransactionAmount().getDebitOrCredit());
            HashMap<String, Amount> innerMap2 = new HashMap<>(){{put(currency,new_Amount2);}};   
            balances.put(userId,innerMap2);
        }

        // return result about the updated balances.
        result.setUserId(userId);
        result.setMessageId(LoadInfo.getMessageId());
        result.setTransactionAmount(balances.get(userId).get(currency));
        return result;
    }
}
