package com.leftkode.financialmanager.application;

import com.fasterxml.uuid.Generators;
import com.leftkode.financialmanager.application.transaction.TransactionApplicationService;
import com.leftkode.financialmanager.application.transaction.TransactionApplicationServiceImpl;
import com.leftkode.financialmanager.service.ServiceConfiguration;
import com.leftkode.financialmanager.service.account.AccountService;
import com.leftkode.financialmanager.service.currency.CurrencyService;
import com.leftkode.financialmanager.service.transaction.TransactionService;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ServiceConfiguration.class)
public class ApplicationConfiguration {

    @Bean
    public TransactionApplicationService transactionApplicationService(
            AccountService accountService,
            CurrencyService currencyService,
            TransactionService transactionService,
            @Qualifier("uuidSupplier") Supplier<String> uuidSupplier
    ) {
        return new TransactionApplicationServiceImpl(accountService, currencyService, transactionService, uuidSupplier);
    }

    @Bean(name = "uuidSupplier")
    public Supplier<String> uuidSupplier() {
        return () -> Generators.timeBasedGenerator().generate().toString();
    }
}
