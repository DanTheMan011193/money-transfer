package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
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






//    @Override
//    public Account transfer(int from, Transfer transfer) {
//
//
////        String sql = "select account_id from account where user_id = ?";
////        SqlRowSet fromUser = jdbcTemplate.queryForRowSet(sql, from);
////        SqlRowSet toUser = jdbcTemplate.queryForRowSet(sql, transfer.getAccountTo());
////        int accountTo;
////        int accountFrom;
////        if (fromUser.next()){
////             accountFrom = fromUser.getInt("account_id");
////        }
////        if (toUser.next()){
////            accountTo = toUser.getInt("account_id");
////        }
//
//            return null;
//    }



    @Override
    public Transfer newTransfer(Transfer transfer) {

        //needs to be more than 0
        if (transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Cannot be 0 or a negative amount");
        }
        //user cant send to themself
        if (transfer.getAccountTo() == transfer.getAccountFrom()){
            throw new IllegalArgumentException("You can't send money to yourself");
        }
        //cant send more than current balance
//        if ( ((viewCurrentBalance(transfer.getAccountFrom()).getBalance()).compareTo(transfer.getAmount()) > 0
//        )){
//                throw new IllegalArgumentException("You broke");
//        }

        String insertSql = "INSERT INTO public.transfer(\n" +
                "\ttransfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "\tVALUES (?, ?, ?, ?, ?) RETURNING transfer_id";

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


    @Override
    public void updateBalance(Account account){
        String sql = "UPDATE public.account\n" +
                "\tSET balance=?\n" +
                "\tWHERE account_id = ?";
        jdbcTemplate.update(sql, account.getBalance(), account.getAccountId());
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
