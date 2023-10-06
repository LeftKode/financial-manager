package com.leftkode.financialmanager.service.currency;

import com.leftkode.financialmanager.infrastructure.persistence.currency.CurrencyRepository;
import com.leftkode.financialmanager.model.Currency;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    public Mono<BigDecimal> convertAmount(BigDecimal amount, String sourceCodeCurrency, String targetCodeCurrency) {
        return Mono.just(amount)
                .doOnNext(am -> log.trace("Converting amount: {} from {} to {}...",
                        amount, sourceCodeCurrency, targetCodeCurrency))
                .flatMap(am -> {
                    if (Objects.equals(sourceCodeCurrency, targetCodeCurrency)) {
                        log.trace("Source and target currencies are the same. Returning initial amount...");
                        return Mono.just(amount);
                    } else {
                        return getCurrencyRate(sourceCodeCurrency)
                                .zipWith(getCurrencyRate(targetCodeCurrency))
                                .map(rates -> am.multiply(rates.getT2())
                                        .divide(rates.getT1(), 5, RoundingMode.HALF_UP));
                    }
                })
                .doOnNext(res -> log.info("Amount conversion completed. [{} {}] → [{} {}]",
                        amount, sourceCodeCurrency, res, targetCodeCurrency))
                .doOnError(e -> log.error("An error occurred during the amount conversion! "
                                + "[Amount: {}, Source: {}, Target: {}]. Error: {}",
                        amount, sourceCodeCurrency, targetCodeCurrency, e.toString())
                );
    }

    private Mono<BigDecimal> getCurrencyRate(String currencyCode) {
        log.trace("Searching for currency ({})...", currencyCode);
        return currencyRepository.findById(currencyCode)
                .doOnNext(currency -> log.trace("Currency ({}) was retrieved.", currencyCode))
                .map(Currency::exchangeRate);
    }
}
