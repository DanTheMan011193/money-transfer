package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;

public interface AccountDao {

    Account viewCurrentBalance(int id);




    Transfer newTransfer(Transfer transfer);
    Transfer getTransferById(Integer newTransferId);

    public void updateBalance(Account account);



}
