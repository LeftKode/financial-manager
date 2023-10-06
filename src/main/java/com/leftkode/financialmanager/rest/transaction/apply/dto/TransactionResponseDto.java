package com.leftkode.financialmanager.rest.transaction.apply.dto;

import com.leftkode.financialmanager.model.Transaction;

/**
 * The response object of `apply-transaction` endpoint.
 *
 * @param transactionId The ID of the created {@link Transaction}
 */
public record TransactionResponseDto(String transactionId) {
}
