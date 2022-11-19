package com.banksystem.account.controller;

import com.banksystem.account.dto.Cards;
import com.banksystem.account.dto.Customer;
import com.banksystem.account.dto.CustomerDetails;
import com.banksystem.account.dto.Loans;
import com.banksystem.account.models.Accounts;
import com.banksystem.account.repository.AccountsRepository;
import com.banksystem.account.service.client.CardsFeignClient;
import com.banksystem.account.service.client.LoansFeignClient;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
//@RequestMapping(value = "accounts")
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

    @GetMapping(value = "/detailsForCustomerSupportApp")
    @CircuitBreaker(name = "detailsForCustomerSupportApp" , fallbackMethod = "customerDetailsFallback")
    public CustomerDetails detailsForCustomerSupportApp(@RequestHeader("banksystem-correlation-id") String correlationId, @RequestBody Customer customer ){
        Optional<Accounts> accounts = accountsRepository.findByCustomerId(customer.customerId());
        Optional<List<Loans>> loans = loansFeignClient.getLoansDetails(correlationId,customer);
        Optional<List<Cards>> cards = cardsFeignClient.getCardDetails(correlationId, customer);

        return new  CustomerDetails(accounts.get(),loans.get(),cards.get());

    }
    private CustomerDetails customerDetailsFallback(String correlationId, Customer customer ,Throwable t){
        Optional<Accounts> accounts = accountsRepository.findByCustomerId(customer.customerId());
        Optional<List<Loans>> loans = loansFeignClient.getLoansDetails(correlationId,customer);
        return   CustomerDetails.withEmptyCardsList(accounts.get(),loans.get());

    }

    @GetMapping(value = "/retryForCustomerDetails")
    @Retry(name = "retryForCustomerDetails", fallbackMethod = "customerDetailsFallback")
    public CustomerDetails retryForCustomerDetails(@RequestHeader("banksystem-correlation-id") String correlationId,@RequestBody Customer customer){
        Optional<Accounts> accounts = accountsRepository.findByCustomerId(customer.customerId());
        Optional<List<Loans>> loans = loansFeignClient.getLoansDetails(correlationId,customer);
        Optional<List<Cards>> cards = cardsFeignClient.getCardDetails(correlationId, customer);

        return new  CustomerDetails(accounts.get(),loans.get(),cards.get());

    }

    @GetMapping(value = "/rateLimiterForCustomerDetails")
    @RateLimiter(name = "rateLimiterForCustomerDetails", fallbackMethod = "customerDetailsFallback")
    //@Bulkhead(name = "rateLimiterForCustomerDetails", fallbackMethod = "customerDetailsFallback")
    public CustomerDetails rateLimiterForCustomerDetails(@RequestHeader("banksystem-correlation-id") String correlationId,@RequestBody Customer customer){
        Optional<Accounts> accounts = accountsRepository.findByCustomerId(customer.customerId());
        Optional<List<Loans>> loans = loansFeignClient.getLoansDetails(correlationId,customer);
        Optional<List<Cards>> cards = cardsFeignClient.getCardDetails(correlationId, customer);

        return new  CustomerDetails(accounts.get(),loans.get(),cards.get());

    }

    @GetMapping(value = "/sayHello")
    @RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
    public String sayHello(){
       return " Hello, welcome to bank system";

    }

    public String sayHelloFallback(Throwable throwable){
        return " Hi, welcome to bank system sayHelloFallback";

    }
}
