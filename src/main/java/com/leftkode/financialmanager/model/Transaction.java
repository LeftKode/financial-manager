package com.leftkode.financialmanager.model;

import java.math.BigDecimal;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * This object holds all the information regarding a financial transaction.
 *
 * @param id              The unique identifier of the transaction
 * @param sourceAccountId The id of the account sending the funds
 * @param targetAccountId The id of the account receiving the funds
 * @param amount          The amount being transferred
 * @param currency        The currency (using ISO 3-Letter Currency Code)
 */
@Table("transaction")
public record Transaction(String id,
                          @Column("source_account_id") Long sourceAccountId,
                          @Column("target_account_id") Long targetAccountId,
                          BigDecimal amount,
                          String currency) {
}
