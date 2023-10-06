package com.leftkode.financialmanager.service;

import com.leftkode.financialmanager.infrastructure.InfrastructureConfiguration;
import com.leftkode.financialmanager.infrastructure.persistence.account.AccountRepository;
import com.leftkode.financialmanager.infrastructure.persistence.currency.CurrencyRepository;
import com.leftkode.financialmanager.infrastructure.persistence.transaction.TransactionRepository;
import com.leftkode.financialmanager.model.Currency;
import com.leftkode.financialmanager.service.account.AccountService;
import com.leftkode.financialmanager.service.account.AccountServiceImpl;
import com.leftkode.financialmanager.service.currency.CurrencyService;
import com.leftkode.financialmanager.service.currency.CurrencyServiceImpl;
import com.leftkode.financialmanager.service.transaction.TransactionService;
import com.leftkode.financialmanager.service.transaction.TransactionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        InfrastructureConfiguration.class
})
public class ServiceConfiguration {

    @Bean
    public AccountService accountService(AccountRepository accountRepository) {
        return new AccountServiceImpl(accountRepository);
    }

    @Bean
    public CurrencyService currencyService(CurrencyRepository currencyRepository) {
        return new CurrencyServiceImpl(currencyRepository);
    }

    @Bean
    public TransactionService transactionService(
            AccountRepository accountRepository,
            TransactionRepository transactionRepository) {
        return new TransactionServiceImpl(accountRepository, transactionRepository);
    }
}
