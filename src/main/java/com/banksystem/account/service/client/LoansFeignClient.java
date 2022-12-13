package com.banksystem.account.service.client;


import com.banksystem.account.dto.Customer;
import com.banksystem.account.dto.Loans;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@FeignClient (  "loans" /*,url = "http://192.168.0.18:7000/loans/"*/ )
public interface LoansFeignClient {

    @RequestMapping(method = RequestMethod.GET , value ="api/v1/loans/loan" ,consumes = "application/json")
    Optional<List<Loans>> findByCustomerIdOrderByStartDtDescLoans(@RequestBody Customer customer);

    @RequestMapping(method = RequestMethod.POST, value = "api/v1/loans/myLoans", consumes = "application/json")
    Optional<List<Loans>> getLoansDetails(@RequestHeader("banksystem-correlation-id") String correlationId,@RequestBody Customer customer);
}
