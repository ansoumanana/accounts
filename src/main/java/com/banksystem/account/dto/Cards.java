package com.banksystem.account.dto;

import java.util.Date;

public record Cards(
        int cardId,
        int customerId,
        String cardNumber,
        String cardType,
        int totalLimit,
        int amountUsed,
        int availableAmount,
        Date createDt

) {
}
