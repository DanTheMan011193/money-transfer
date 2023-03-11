package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url) {
        this.baseUrl = url;
    }

    private HttpEntity makeEntity(String token){

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(headers);
        return entity;
    }
//    private HttpEntity<Integer> makeEntity(int id) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        Integer requestBody = id;
//        HttpEntity<Integer> entity = new HttpEntity<>(requestBody, headers);
//        return entity;
//    }

    public User[] listUsers(){
        User[] users = null;
        try {
            users = restTemplate.getForObject(baseUrl + "/users", User[].class);
        }
     catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
       }
        return users;
    }

    public Account getBalance(String token){
        Account balance = null;
        try {
            balance = restTemplate.exchange(baseUrl + "/account", HttpMethod.GET, makeEntity(token), Account.class).getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

//        public Transfer transferMoney(String token){
//
//                    Transfer amount = null;
//            try {
//                amount = restTemplate.exchange(baseUrl + "/transfer", HttpMethod.PUT, makeEntity(token), Transfer.class).getBody();
//            }
//            catch (RestClientResponseException | ResourceAccessException e) {
//                BasicLogger.log(e.getMessage());
//            }
//            return amount;
//        }

    public boolean transferMoney(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);

        boolean success = false;
        try {
            restTemplate.put(baseUrl + "/transfer" + transfer.getTransferId(), entity);
            success = true;
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return success;
    }






}
