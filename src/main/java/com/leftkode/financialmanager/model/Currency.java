package com.leftkode.financialmanager.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * This object holds all the information regarding a system's account.
 *
 * @param currencyCode The currency code (using ISO 3-Letter Currency Code)
 * @param currencyName The name of the currency
 * @param exchangeRate the value of one currency for the purpose of conversion to another
 * @param createdAt    The creation date-time
 * @param updatedAt    The date-time that the record was updated
 */
@Table("currency")
public record Currency(@Id @Column("currency_code") String currencyCode,
                       @Column("currency_name") String currencyName,
                       @Column("exchange_rate") BigDecimal exchangeRate,
                       @Column("created_at") LocalDateTime createdAt,
                       @Column("updated_at") LocalDateTime updatedAt) {


}
