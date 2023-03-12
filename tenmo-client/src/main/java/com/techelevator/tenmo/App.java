package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final User user = new User();
    private final AccountService accountService = new AccountService(API_BASE_URL);
    private final Account account = new Account();
    private final Transfer transfer = new Transfer();

    private AuthenticatedUser currentUser;


    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
                accountService.setAuthToken(currentUser.getToken());
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }



	private void viewCurrentBalance() {
        Account balance = accountService.getBalance(currentUser.getToken());
        System.out.println(balance.getBalance());

		// TODO Auto-generated method stub
		
	}

	private void viewTransferHistory() {



        Transfer[] transferList = accountService.listTransfers(currentUser.getToken());
        for (Transfer transfer : transferList) {
            System.out.println(transfer);
        }


		// TODO Auto-generated method stub
		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
        User[] userList = accountService.listUsers();
        for (User user : userList) {
            System.out.println(user);
        }
        int transferToUserId = consoleService.promptForInt("Enter the id of the person you would like to send money to: ");
        BigDecimal transferAmount = consoleService.promptForBigDecimal("Enter the amount to transfer: ");

        Transfer transfer = new Transfer();
        transfer.setTransferTypeId(2);
        transfer.setTransferStatusId(2);
        transfer.setAccountFrom(currentUser.getUser().getId());
        transfer.setAccountTo(transferToUserId);
        transfer.setAmount(transferAmount);

        Transfer newTransfer = accountService.transferMoney(currentUser.getToken(),transfer);
        if (newTransfer != null) {
            System.out.println("Transfer successful!");
        } else {
            System.out.println("Transfer failed.");
        }

        // TODO Auto-generated method stub
    }

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
