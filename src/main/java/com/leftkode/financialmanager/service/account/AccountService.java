package com.leftkode.financialmanager.service.account;

import com.leftkode.financialmanager.model.Account;
import reactor.core.publisher.Mono;

/**
 * This service is related to {@link Account} actions.
 */
public interface AccountService {

    /**
     * Retrieves the {@link Account} which has the given id
     *
     * @param id The id of the account
     * @return Return a Mono with the {@link Account} that matched.
     *         Returns Mono empty if no records matched
     */
    Mono<Account> getAccount(Long id);

}
