package com.leftkode.financialmanager.service.currency;

import com.leftkode.financialmanager.model.Currency;
import java.math.BigDecimal;
import reactor.core.publisher.Mono;

/**
 * This service is related to {@link Currency} actions.
 */
public interface CurrencyService {

    /**
     * Converts the given amount from the given source currency to the target one
     *
     * @param amount             The amount that will be converted
     * @param sourceCodeCurrency The currency that currently the amount has
     * @param targetCodeCurrency The currency that the returned amount will have
     * @return The amount in the target currency
     */
    Mono<BigDecimal> convertAmount(BigDecimal amount, String sourceCodeCurrency, String targetCodeCurrency);
}
