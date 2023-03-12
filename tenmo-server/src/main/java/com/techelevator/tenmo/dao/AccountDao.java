package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    Account viewCurrentBalance(int id);




    Transfer newTransfer(Transfer transfer);
    Transfer getTransferById(Integer newTransferId);

    // UPDATES ACCOUNT BALANCE IN ACCOUNT TABLE


    // UPDATES ACCOUNT BALANCE IN ACCOUNT TABLE
    void updateBalance(int fromAccount, int toAccount, BigDecimal transferAmount);

    List<Transfer> getAllTransactions(int fromAccount);


    // void addBalance(Account account, BigDecimal transferAmount);


   // void subtractBalance(Account account, BigDecimal transferAmount);
}
