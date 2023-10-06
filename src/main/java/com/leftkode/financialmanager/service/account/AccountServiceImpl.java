package com.leftkode.financialmanager.service.account;

import com.leftkode.financialmanager.infrastructure.persistence.account.AccountRepository;
import com.leftkode.financialmanager.model.Account;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Mono<Account> getAccount(Long id) {
        return accountRepository.findById(id);
    }
}
