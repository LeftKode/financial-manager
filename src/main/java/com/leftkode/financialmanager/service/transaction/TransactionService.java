package com.leftkode.financialmanager.service.transaction;

import com.leftkode.financialmanager.model.Transaction;
import java.math.BigDecimal;
import reactor.core.publisher.Mono;

/**
 * This service is related to {@link Transaction} actions.
 */
public interface TransactionService {

    /**
     * Creates and persists a {@link Transaction}.
     *
     * @param transaction  The object that holds the details of the transaction
     * @param creditAmount The amount which will be credited to the target account of the transaction
     * @return The id of the created transaction
     */
    Mono<String> applyTransaction(final Transaction transaction,
                                  final BigDecimal creditAmount);
}
