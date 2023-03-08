package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
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
    JdbcTemplate jdbcTemplate = new JdbcTemplate();

    @Override
    public Account viewCurrentBalance(int id) {

        Account account = new Account();
        String sql = "SELECT balance\n" +
                "\tFROM public.account";
       SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
       if (results.next()){
           account = mapRowToAccount(results);
       }
        return account;
    }

    private Account mapRowToAccount(SqlRowSet rs) {
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));

        return account;
    }


}
