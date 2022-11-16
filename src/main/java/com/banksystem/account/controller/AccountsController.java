package com.banksystem.account.controller;

import com.banksystem.account.dto.Cards;
import com.banksystem.account.dto.Customer;
import com.banksystem.account.dto.CustomerDetails;
import com.banksystem.account.dto.Loans;
import com.banksystem.account.models.Accounts;
import com.banksystem.account.repository.AccountsRepository;
import com.banksystem.account.service.client.CardsFeignClient;
import com.banksystem.account.service.client.LoansFeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "accounts")
public class AccountsController {

    private final AccountsRepository accountsRepository;
    private final LoansFeignClient loansFeignClient;
    private final CardsFeignClient cardsFeignClient;

    public AccountsController(AccountsRepository accountsRepository, LoansFeignClient loansFeignClient, CardsFeignClient cardsFeignClient) {
        this.accountsRepository = accountsRepository;
        this.loansFeignClient = loansFeignClient;
        this.cardsFeignClient = cardsFeignClient;
    }


    @GetMapping(value = "/{customerID}")
    public Optional<Accounts> findByCustomerId( @PathVariable int customerID){

        return accountsRepository.findByCustomerId(customerID);

    }

    @GetMapping(value = "/customerDetails")
    public CustomerDetails custumerDetails(@RequestBody Customer customer){
        Optional<Accounts> accounts = accountsRepository.findByCustomerId(customer.customerId());
        Optional<List<Loans>> loans = loansFeignClient.getLoansDetails(customer);
        Optional<List<Cards>> cards = cardsFeignClient.getCardDetails(customer);

        return new  CustomerDetails(accounts.get(),loans.get(),cards.get());

    }

    @GetMapping(value = "/getcustomerDetails")
    public CustomerDetails getCustumerDetails(@RequestBody Customer customer){
        Optional<Accounts> accounts = accountsRepository.findByCustomerId(customer.customerId());
        Optional<List<Loans>> loans = loansFeignClient.getLoansDetails(customer);
        Optional<List<Cards>> cards = cardsFeignClient.getCardDetails(customer);

        return new  CustomerDetails(accounts.get(),loans.get(),cards.get());

    }
}
