package com.leftkode.financialmanager.service.transaction;

import com.leftkode.financialmanager.infrastructure.persistence.account.AccountRepository;
import com.leftkode.financialmanager.infrastructure.persistence.transaction.TransactionRepository;
import com.leftkode.financialmanager.model.Transaction;
import jakarta.validation.ValidationException;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Mono<String> applyTransaction(final Transaction transaction,
                                         final BigDecimal creditAmount) {
        log.trace("Applying the transaction with id: {}...", transaction.id());
        return debitSourceAccount(transaction)
                .then(Mono.defer(() -> creditTargetAccount(transaction, creditAmount)))
                .then(Mono.defer(() -> saveTransaction(transaction)))
                .map(Transaction::id);
    }

    private Mono<Transaction> saveTransaction(Transaction transaction) {
        log.trace("Saving transaction with id: {}", transaction.id());
        return transactionRepository.save(transaction)
                .doOnNext(tr -> log.trace("Transaction with id: {} was saved successfully.", tr.id()));
    }

    private Mono<Void> debitSourceAccount(Transaction transaction) {
        log.trace("Debiting source account... [AccountId: {}, Amount: {}]", transaction.sourceAccountId(), transaction.amount());
        return accountRepository.reduceBalance(transaction.amount(), transaction.sourceAccountId())
                .onErrorMap(DataAccessResourceFailureException.class,
                        e -> handleInsufficientBalanceException(e, transaction.sourceAccountId()))
                .doOnSuccess(ignored -> log.trace("Source account was debited successfully."));
    }

    private Throwable handleInsufficientBalanceException(DataAccessResourceFailureException e, Long accountId) {
        if (e.getMessage().contains("chk_balance_not_negative")) {
            log.info("Insufficient balance! [AccountId: {}]", accountId);
            return new ValidationException("sourceAccountId: must have sufficient balance");
        } else {
            return e;
        }
    }

    private Mono<Void> creditTargetAccount(Transaction transaction, BigDecimal creditAmount) {
        log.trace("Crediting target account... [AccountId: {}, Amount: {}]",
                transaction.targetAccountId(), creditAmount);
        return accountRepository.addBalance(creditAmount, transaction.targetAccountId())
                .doOnRequest(ignored -> log.trace("Target source was credited successfully."));
    }
}
