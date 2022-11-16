package com.banksystem.account.dto;

import java.util.Date;

public record Loans (

        int loanNumber,

        int customerId,

        Date startDt,

        String loanType,

        int totalLoan,

        int amountPaid,

        int outstandingAmount,

        String createDt
){
}
