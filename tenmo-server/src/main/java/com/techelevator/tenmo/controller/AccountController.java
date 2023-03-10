package com.techelevator.tenmo.controller;

import javax.validation.Valid;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

/**
 * Controller to authenticate users.
 */
@RestController
public class AccountController {


    private UserDao userDao;
    private AccountDao accountDao;

    public AccountController(UserDao userDao, AccountDao accountDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> listUsers() {
        return userDao.findAll();
    }

    @RequestMapping(path = "/users/{id}", method = RequestMethod.GET)
    public User listUserById(@PathVariable int id) {
        return userDao.getUserById(id);
    }

    @RequestMapping(path = "/account", method = RequestMethod.GET)
    public Account viewCurrentBalance(Principal principal){
        int id = userDao.findByUsername(principal.getName()).getId();
        return accountDao.viewCurrentBalance(id);
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public Transfer newTransfer(@RequestBody Transfer transfer){
        return accountDao.newTransfer(transfer);
    }

    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.PUT)
    public Transfer newTransferUpdateAccount(@RequestBody Transfer transfer) {
        return accountDao.newTransfer(transfer);
    }
}















//    @RequestMapping(path = "transfer/{id}", method = RequestMethod.GET)
//            public Transfer getTransferById(){
//        }


































