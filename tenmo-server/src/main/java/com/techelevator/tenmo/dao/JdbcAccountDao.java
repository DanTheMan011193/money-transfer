package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Component
public class JdbcAccountDao implements AccountDao{
    private JdbcTemplate jdbcTemplate;
    User user = new User();
    Account account  = new Account();

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account viewCurrentBalance(int id) {

        Account account = new Account();
        String sql = "SELECT balance, user_id, account_id " +
                " FROM public.account " +
                " WHERE user_id = ?";
       SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
       if (results.next()){
           account = mapRowToAccount(results);
       }
        return account;
    }

    private Boolean viewTransAmount(int id, BigDecimal amount) {
        Boolean result = false;
        Account account = new Account();
        String sql = "SELECT (balance > ?) as XferLessThanBalance\n" +
                "FROM account\n" +
                "Where user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, amount.doubleValue(), id);
        if (results.next()){
            result = results.getBoolean("XferLessThanBalance");
        }
        return result;
    }

    @Override
    public Transfer newTransfer(Transfer transfer) {

        if (transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Cannot be 0 or a negative amount");
        }
        if (transfer.getAccountTo() == transfer.getAccountFrom()){
            throw new IllegalArgumentException("You can't send money to yourself");
        }
        if (!viewTransAmount(transfer.getAccountFrom(), transfer.getAmount())){
            throw new IllegalArgumentException("You CANNOT SEND MORE MONEY THAN BALANCE");
        }
        String insertSql = "INSERT INTO public.transfer(\n" +
                "\ttransfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "\tVALUES (?, ?, (SELECT account_id FROM account where user_id = ?), (SELECT account_id FROM account where user_id = ?), ?) RETURNING transfer_id";

        Integer newTransferId = jdbcTemplate.queryForObject(insertSql, Integer.class,
                transfer.getTransferTypeId(), transfer.getTransferStatusId(),
                transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());


        return getTransferById(newTransferId);
    }

    @Override
    public Transfer getTransferById(Integer newTransferId) {
        Transfer transfer = null;
        String sql = "select transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "from transfer where transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, newTransferId);
        if (results.next()){
            transfer = mapRowToTransfer(results);
        }
        return transfer;
    }
    //UPDATES BALANCE OF TO ACCOUNT IN XFER TABLE
    private void addBalance(int accountId, BigDecimal transferAmount){
        String sql = "UPDATE public.account\n" +
                "\tSET balance=?\n" +
                "\tWHERE account_id = ?";
        BigDecimal currentBalance = account.getBalance();
        BigDecimal updatedBalance = (currentBalance.add(transferAmount));

        jdbcTemplate.update(sql, updatedBalance, account.getAccountId());
    }
    //UPDATES BALANCE OF FROM ACCOUNT IN XFER TABLE
    private void subtractBalance(int accountId, BigDecimal transferAmount){
        String sql = "UPDATE public.account\n" +
                "\tSET balance=?\n" +
                "\tWHERE account_id = ?";
        BigDecimal currentBalance = account.getBalance();
        BigDecimal updatedBalance = (currentBalance.subtract(transferAmount));

        jdbcTemplate.update(sql, updatedBalance, account.getAccountId());
    }
    // UPDATES ACCOUNT BALANCE IN ACCOUNT TABLE
    @Override
    public void updateBalance(int fromAccount, int toAccount, BigDecimal transferAmount){
        String sql = "BEGIN TRANSACTION;\n" +
                "UPDATE account\n" +
                "SET balance = balance - ?\n" +
                "WHERE account_id = (SELECT account_id FROM account where user_id = ?);\n" +
                "\n" +
                "UPDATE account\n" +
                "SET balance = balance + ?\n" +
                "WHERE account_id = (SELECT account_id FROM account where user_id = ?);\n" +
                "\n" +
                "COMMIT";
//        //BigDecimal fromAmount = subtractBalance(accountId,transferAmount);
//        subtractBalance(fromAccount.getAccountId(), transferAmount);
//        addBalance(toAccount.getAccountId(),transferAmount);
        jdbcTemplate.update(sql, transferAmount, fromAccount, transferAmount, toAccount);


    }

    @Override
    public List<Transfer> getAllTransactions(int fromAccount) {
        List<Transfer> returnedList = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount\n" +
                "\tFROM public.transfer\n" +
                "\twhere account_from = (select account_id from account where user_id = ?)\n" +
                "\tor account_to = (select account_id from account where user_id = ?)";

        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, fromAccount, fromAccount);
        while (results.next()){
            Transfer transfer = new Transfer();
            transfer.setTransferId(results.getInt("transfer_id"));
            transfer.setTransferTypeId(results.getInt("transfer_type_id"));
            transfer.setTransferStatusId(results.getInt("transfer_status_id"));
            transfer.setAccountFrom(results.getInt("account_from"));
            transfer.setAccountTo(results.getInt("account_to"));
            transfer.setAmount(results.getBigDecimal("amount"));
            returnedList.add(transfer);
        }
            return returnedList;

    }


    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));

        return account;
    }
    private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getInt("transfer_id"));
        transfer.setTransferTypeId(rs.getInt("transfer_type_id"));
        transfer.setTransferStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));

        return transfer;
    }

}















