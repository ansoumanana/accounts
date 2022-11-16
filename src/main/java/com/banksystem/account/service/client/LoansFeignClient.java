package com.banksystem.account.service.client;


import com.banksystem.account.dto.Customer;
import com.banksystem.account.dto.Loans;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Optional;

@FeignClient ( name = "loans" ,url = "http://192.168.0.18:7000/loans/" )
public interface LoansFeignClient {

    @RequestMapping(method = RequestMethod.GET , value ="loan" ,consumes = "application/json")
    Optional<List<Loans>> findByCustomerIdOrderByStartDtDescLoans(@RequestBody Customer customer);

    @RequestMapping(method = RequestMethod.POST, value = "myLoans", consumes = "application/json")
    Optional<List<Loans>> getLoansDetails(@RequestBody Customer customer);
}
