package com.banksystem.account.dto;

import com.banksystem.account.models.Accounts;

import java.util.Collections;
import java.util.List;

public record CustomerDetails(Accounts accounts, List<Loans> loans, List<Cards> cards) {
    public  static CustomerDetails withEmptyCardsList(Accounts accounts, List<Loans> loans) {
        return new CustomerDetails( accounts ,loans , Collections.EMPTY_LIST);
    }
}
