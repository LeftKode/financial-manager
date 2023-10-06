package com.leftkode.financialmanager.infrastructure.persistence.transaction;

import com.leftkode.financialmanager.model.Transaction;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

/**
 * A repository which holds all the queries related to the {@link Transaction}.
 */
public interface TransactionRepository extends R2dbcRepository<Transaction, String> {

}
