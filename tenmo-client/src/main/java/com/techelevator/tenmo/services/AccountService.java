package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class AccountService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public AccountService(String url) {
        this.baseUrl = url;
    }

    public User[] listUsers(){
        User[] users = null;
        try {
            users = restTemplate.getForObject(baseUrl + "/users", User[].class);
        }
     catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
       }
        System.out.println(users);
        return users;
    }





}
