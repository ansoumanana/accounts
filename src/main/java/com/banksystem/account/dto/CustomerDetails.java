package com.banksystem.account.dto;

import com.banksystem.account.models.Accounts;

import java.util.List;

public record CustomerDetails(
        Accounts accounts,
        List<Loans> loans,
        List<Cards> cards
) {
}
