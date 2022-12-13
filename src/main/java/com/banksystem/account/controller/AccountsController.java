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
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Application;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/accounts" ,produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountsController {

    private final AccountsRepository accountsRepository;
    private final LoansFeignClient loansFeignClient;
    private final CardsFeignClient cardsFeignClient;

    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

    public AccountsController(AccountsRepository accountsRepository, LoansFeignClient loansFeignClient, CardsFeignClient cardsFeignClient) {
        this.accountsRepository = accountsRepository;
        this.loansFeignClient = loansFeignClient;
        this.cardsFeignClient = cardsFeignClient;
    }


    @Operation(summary = "Get  Accounts by Customer id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Found the Account", content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Accounts.class))}),
            @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content)})
    @GetMapping(value = "/{customerID}")
    @Timed(value ="findByCustomerId.time", description = " Time taken to return account")
    public ResponseEntity<Accounts> findByCustomerId( @PathVariable int customerID){

        final  Optional<Accounts> accounts = accountsRepository.findByCustomerId(customerID);
        if (accounts.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(accounts.get());
    }

    @Timed(value ="detailsForCustomerSupportApp.time", description = " Time taken to return Customer Details")
    @GetMapping(value = "/detailsForCustomerSupportApp")
    @CircuitBreaker(name = "detailsForCustomerSupportApp" , fallbackMethod = "customerDetailsFallback")
    public CustomerDetails detailsForCustomerSupportApp(@RequestHeader("banksystem-correlation-id") String correlationId, @RequestBody Customer customer ){
        logger.info(" Start call of detailsForCustomerSupportApp ");
        Optional<Accounts> accounts = accountsRepository.findByCustomerId(customer.customerId());
        Optional<List<Loans>> loans = loansFeignClient.getLoansDetails(correlationId,customer);
        Optional<List<Cards>> cards = cardsFeignClient.getCardDetails(correlationId, customer);
        logger.info(" End call of detailsForCustomerSupportApp ");
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
