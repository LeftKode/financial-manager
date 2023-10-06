package com.leftkode.financialmanager.application.transaction;

import com.leftkode.financialmanager.model.Account;
import com.leftkode.financialmanager.model.Transaction;
import com.leftkode.financialmanager.rest.transaction.apply.dto.TransactionRequestDto;
import com.leftkode.financialmanager.rest.transaction.apply.dto.TransactionResponseDto;
import com.leftkode.financialmanager.service.account.AccountService;
import com.leftkode.financialmanager.service.currency.CurrencyService;
import com.leftkode.financialmanager.service.transaction.TransactionService;
import jakarta.validation.ValidationException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

@Slf4j
@RequiredArgsConstructor
public class TransactionApplicationServiceImpl implements TransactionApplicationService {

    private final AccountService accountService;

    private final CurrencyService currencyService;

    private final TransactionService transactionService;

    private final Supplier<String> supplierId;

    public Mono<TransactionResponseDto> applyTransaction(Mono<TransactionRequestDto> transactionRequestDto) {
        return transactionRequestDto
                .log()
                .doOnNext(dto -> log.trace("A transaction request has been received. {}", dto))
                .flatMap(this::checkIfAccountIdsAreTheSame)
                .flatMap(this::getSourceAccountCurrencyAndCreateTransaction)
                .flatMap(this::getTargetAccountCurrency)
                .flatMap(this::applyTransaction)
                .map(TransactionResponseDto::new)
                .doOnNext(res -> log.info("Transaction was applied successfully. [transactionId: {}]", res))
                .doOnError(error -> log.trace("Transaction was not applied. Error: [{}]", error.getMessage()));
    }

    private Mono<TransactionRequestDto> checkIfAccountIdsAreTheSame(TransactionRequestDto transactionRequestDto) {
        log.trace("Checking if the two given accounts id have the same value...");
        return Mono.just(transactionRequestDto)
                .filter(dto -> !Objects.equals(dto.sourceAccountId(), dto.targetAccountId()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("The account ids have the same value! Returning a ValidationException...");
                    return Mono.error(
                            new ValidationException("sourceAccountId, targetAccountId: must not have the same value"));
                }))
                .doOnNext(dto -> log.trace("The account ids are different. [Source:{}, Target:{}]",
                        dto.sourceAccountId(), dto.targetAccountId()));
    }

    private Mono<Transaction> getSourceAccountCurrencyAndCreateTransaction(TransactionRequestDto dto) {
        return getAccount(dto.sourceAccountId(), "sourceAccountId")
                .doOnNext(account -> log.trace("Mapping transaction dto to entity..."))
                .map(sourceAccount -> map(dto, sourceAccount.currency()))
                .doOnNext(transaction -> log.trace("Transaction entity was created. {}", transaction));
    }

    private Mono<Tuple2<Transaction, BigDecimal>> getTargetAccountCurrency(Transaction transaction) {
        return getAccount(transaction.targetAccountId(), "targetAccountId")
                .flatMap(account -> getCreditAmount(transaction, account.currency())
                        .map(creditAmount -> Tuples.of(transaction, creditAmount)));
    }

    private Mono<Account> getAccount(Long accountId, String fieldName) {
        log.trace("Retrieving account with id: {}...", accountId);
        return accountService.getAccount(accountId)
                .switchIfEmpty(Mono.defer(() -> {
                    log.trace("There is no account with id: {}!", accountId);
                    return Mono.error(new ValidationException(fieldName + ": must be an existing account id"));
                }))
                .doOnNext(account -> log.trace("Account with id: {} was retrieved successfully.", accountId));
    }

    private Mono<BigDecimal> getCreditAmount(Transaction transaction, String currency) {
        return currencyService.convertAmount(transaction.amount(), transaction.currency(), currency);
    }

    private Mono<String> applyTransaction(Tuple2<Transaction, BigDecimal> transactionAndCreditAmount) {
        return transactionService.applyTransaction(
                transactionAndCreditAmount.getT1(), transactionAndCreditAmount.getT2());
    }

    private Transaction map(TransactionRequestDto transactionRequestDto, String currency) {
        return new Transaction(supplierId.get(),
                transactionRequestDto.sourceAccountId(),
                transactionRequestDto.targetAccountId(),
                transactionRequestDto.amount(),
                currency);
    }
}
