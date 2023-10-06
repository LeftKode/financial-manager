package com.leftkode.financialmanager.infrastructure.persistence.currency;

import com.leftkode.financialmanager.model.Currency;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * A repository which holds all the queries related to the {@link Currency}.
 */
public interface CurrencyRepository extends R2dbcRepository<Currency, String> {

}
