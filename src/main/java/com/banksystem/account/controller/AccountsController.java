package com.banksystem.account.controller;

import com.banksystem.account.models.Accounts;
import com.banksystem.account.repository.AccountsRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "accounts")
public class AccountsController {

    private final AccountsRepository accountsRepository;

    public AccountsController(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }


    @GetMapping(value = "/{customerID}")
    public Optional<Accounts> findByCustomerId( @PathVariable int customerID){

        return accountsRepository.findByCustomerId(customerID);

    }
}
