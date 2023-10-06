package com.leftkode.financialmanager.service.account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.leftkode.financialmanager.infrastructure.persistence.account.AccountRepository;
import com.leftkode.financialmanager.model.Account;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class AccountServiceTest {

    @Mock
    public AccountRepository accountRepository;

    @InjectMocks
    public AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        reset(accountRepository);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void test_getAccount_when_accountExists_returns_MonoOfFoundAccount() {
        // ===== GIVEN =====
        final Account account =
                new Account(1L, BigDecimal.valueOf(400), "EUR",
                        LocalDateTime.of(2023, 10, 2, 10, 45, 30));

        // ===== WHEN =====
        when(accountRepository.findById(any(Long.class))).thenReturn(Mono.just(account));

        // === EXECUTE ====
        StepVerifier.create(accountService.getAccount(1L))
                .expectNext(account)
                .verifyComplete();

        // ===== THEN =====
        verify(accountRepository).findById(1L);
    }

    @Test
    void test_getAccount_when_accountExists_returns_MonoEmpty() {

        // ===== WHEN =====
        when(accountRepository.findById(any(Long.class))).thenReturn(Mono.empty());

        // === EXECUTE ====
        StepVerifier.create(accountService.getAccount(1L))
                .verifyComplete();

        // ===== THEN =====
        verify(accountRepository).findById(1L);
    }
}