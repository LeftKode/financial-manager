package com.leftkode.financialmanager.rest.transaction.apply.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * The object that holds all the information coming form the `apply-transaction` endpoint.
 *
 * @param sourceAccountId The id of the account sending the funds
 * @param targetAccountId The id of the account receiving the funds
 * @param amount          The amount being transferred
 */
public record TransactionRequestDto(@NotNull Long sourceAccountId,
                                    @NotNull Long targetAccountId,
                                    @NotNull @Min(0) @Digits(integer=13, fraction=5) BigDecimal amount) {
}
