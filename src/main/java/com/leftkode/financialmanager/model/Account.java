package com.leftkode.financialmanager.model;

import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * This object holds all the information regarding a system's account.
 *
 * @param id        The unique identifier of this account
 * @param balance   The amount of money that the account owns
 * @param currency  The currency of the account's balance
 * @param createdAt The datetime when the account was created
 */
@Table("account")
public record Account(@Id Long id,
                      @Digits(integer = 18, fraction = 5) BigDecimal balance,
                      String currency,
                      @Column("created_at") LocalDateTime createdAt) {
}
