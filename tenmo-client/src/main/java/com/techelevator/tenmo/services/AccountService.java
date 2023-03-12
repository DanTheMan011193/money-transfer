package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class AccountService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser currentUser;



    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

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


    private HttpEntity<Transfer> makeTransferEntity(String token, Transfer transfer){

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
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



    public Transfer transferMoney(String token, Transfer transfer){
        Transfer newTransfer = null;

        try {
            newTransfer = restTemplate.postForObject(baseUrl + "/transfer",
                    makeTransferEntity(token, transfer), Transfer.class);
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return newTransfer;
    }

    public Transfer[] listTransfers(String token){
        Transfer[] transfers = null;
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "/transfer",HttpMethod.GET, makeEntity(token), Transfer[].class);
            transfers = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

}
