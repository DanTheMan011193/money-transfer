package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

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






}
