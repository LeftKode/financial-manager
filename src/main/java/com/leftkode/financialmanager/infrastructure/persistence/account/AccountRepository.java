package com.leftkode.financialmanager.infrastructure.persistence.account;

import com.leftkode.financialmanager.model.Account;
import java.math.BigDecimal;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

/**
 * A repository which holds all the queries related to the {@link Account}.
 */
public interface AccountRepository extends R2dbcRepository<Account, Long> {

    /**
     * Credits an account's balance.
     *
     * @param amount The amount that will be added to the account's balance
     * @param id     The ID of the account
     * @return A Mono Void
     */
    @Query("update account set balance = balance + :amount where id = :id")
    Mono<Void> addBalance(BigDecimal amount, Long id);

    /**
     * Subtracts the given amount from the account's balance.
     * If the new balance is less than 0, the action will fail and an exception returns
     * as there is a constraint to the database's schema.
     *
     * @param amount The amount that will be subtracted from the account's balance
     * @param id     The ID of the account
     * @return A Mono Void
     */
    @Query("update account set balance = balance - :amount where id = :id")
    Mono<Void> reduceBalance(BigDecimal amount, Long id);

}
