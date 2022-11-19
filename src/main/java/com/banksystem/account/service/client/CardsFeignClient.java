package com.banksystem.account.service.client;

import com.banksystem.account.dto.Cards;
import com.banksystem.account.dto.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@FeignClient( "cards" /*,url = "http://192.168.0.18:9000/cards/"*/)
public interface CardsFeignClient {


    @RequestMapping(method = RequestMethod.GET ,value = "card", consumes = "application/json")
    Optional<List<Cards>>  findCustomerById(@RequestBody Customer customer);

    @RequestMapping(method = RequestMethod.POST, value = "myCards", consumes = "application/json")
    Optional<List<Cards>> getCardDetails(@RequestHeader("banksystem-correlation-id") String correlationId,@RequestBody Customer customer);
}
