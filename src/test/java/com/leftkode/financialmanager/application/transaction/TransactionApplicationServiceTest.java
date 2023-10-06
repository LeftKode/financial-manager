package com.leftkode.financialmanager.application.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.leftkode.financialmanager.model.Account;
import com.leftkode.financialmanager.model.Currency;
import com.leftkode.financialmanager.model.Transaction;
import com.leftkode.financialmanager.rest.transaction.apply.dto.TransactionRequestDto;
import com.leftkode.financialmanager.service.account.AccountService;
import com.leftkode.financialmanager.service.currency.CurrencyService;
import com.leftkode.financialmanager.service.transaction.TransactionService;
import jakarta.validation.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class TransactionApplicationServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private CurrencyService currencyService;

    @Mock
    private TransactionService transactionService;

    private TransactionApplicationServiceImpl transactionApplicationService;

    @BeforeEach
    void setUp() {
        reset(accountService, currencyService, transactionService);
        transactionApplicationService = new TransactionApplicationServiceImpl(
                accountService, currencyService, transactionService, () -> "transaction-id"
        );
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(accountService, currencyService, transactionService);
    }

    // ===== Currency ======
    final Currency currency = new Currency("EUR", "Euro", BigDecimal.valueOf(0.85000),
            LocalDateTime.of(2023, 10, 2, 10, 30, 45),
            LocalDateTime.of(2023, 10, 3, 12, 30, 50));

    // ===== Accounts ======
    final LocalDateTime account1CreatedAt = LocalDateTime.of(2023, 9, 13, 11, 12, 00);
    final LocalDateTime account2CreatedAt = LocalDateTime.of(2023, 9, 15, 9, 5, 30);

    @Test
    void test_applyTransaction() {
        // ===== GIVEN =====
        final TransactionRequestDto transactionRequestDto = new TransactionRequestDto(1L, 2L, BigDecimal.valueOf(5.4));

        final Account sourceAccount =
                new Account(1L, BigDecimal.valueOf(400), currency.currencyCode(), account1CreatedAt);
        final Account targetAccount =
                new Account(2L, BigDecimal.valueOf(300), currency.currencyCode(), account2CreatedAt);

        // ===== WHEN =====
        when(accountService.getAccount(1L)).thenReturn(Mono.just(sourceAccount));
        when(accountService.getAccount(2L)).thenReturn(Mono.just(targetAccount));
        when(currencyService.convertAmount(any(BigDecimal.class), any(String.class), any(String.class)))
                .thenReturn(Mono.just(BigDecimal.valueOf(.35294)));
        when(transactionService.applyTransaction(any(Transaction.class), any(BigDecimal.class)))
                .thenReturn(Mono.just("transaction-id"));

        // === EXECUTE ====
        StepVerifier.create(transactionApplicationService.applyTransaction(Mono.just(transactionRequestDto)))
                .assertNext(dto -> {
                    assertNotNull(dto);
                    assertEquals("transaction-id", dto.transactionId());
                }).verifyComplete();

        // ===== THEN =====
        verify(accountService).getAccount(1L);
        verify(accountService).getAccount(2L);
        verify(currencyService).convertAmount(BigDecimal.valueOf(5.4),
                sourceAccount.currency(), targetAccount.currency());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionService).applyTransaction(transactionCaptor.capture(), eq(BigDecimal.valueOf(.35294)));
        assertEquals(new Transaction("transaction-id", 1L, 2L, BigDecimal.valueOf(5.4), currency.currencyCode()),
                transactionCaptor.getValue());
    }

    @Test
    void test_applyTransaction_when_transactionServiceReturnsMonoError_ReturnsMonoError() {
        // ===== GIVEN =====
        final TransactionRequestDto transactionRequestDto = new TransactionRequestDto(1L, 2L, BigDecimal.valueOf(5.4));

        final Account sourceAccount =
                new Account(1L, BigDecimal.valueOf(400), currency.currencyCode(), account1CreatedAt);
        final Account targetAccount =
                new Account(2L, BigDecimal.valueOf(300), currency.currencyCode(), account2CreatedAt);

        // ===== WHEN =====
        when(accountService.getAccount(1L)).thenReturn(Mono.just(sourceAccount));
        when(accountService.getAccount(2L)).thenReturn(Mono.just(targetAccount));
        when(currencyService.convertAmount(any(BigDecimal.class), any(String.class), any(String.class)))
                .thenReturn(Mono.just(BigDecimal.valueOf(.35294)));
        RuntimeException runtimeException = mock(RuntimeException.class);
        when(transactionService.applyTransaction(any(Transaction.class), any(BigDecimal.class)))
                .thenReturn(Mono.error(runtimeException));

        // === EXECUTE ====
        StepVerifier.create(transactionApplicationService.applyTransaction(Mono.just(transactionRequestDto)))
                .expectErrorMatches(runtimeException::equals)
                .verify();

        // ===== THEN =====
        verify(accountService).getAccount(1L);
        verify(accountService).getAccount(2L);
        verify(currencyService).convertAmount(BigDecimal.valueOf(5.4),
                sourceAccount.currency(), targetAccount.currency());

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionService).applyTransaction(transactionCaptor.capture(), eq(BigDecimal.valueOf(.35294)));
        assertEquals(new Transaction("transaction-id", 1L, 2L, BigDecimal.valueOf(5.4), currency.currencyCode()),
                transactionCaptor.getValue());
    }

    @Test
    void test_applyTransaction_when_conversionOfAmountWasFailed_ReturnsMonoError() {
        // ===== GIVEN =====
        final TransactionRequestDto transactionRequestDto = new TransactionRequestDto(1L, 2L, BigDecimal.valueOf(5.4));

        final Account sourceAccount =
                new Account(1L, BigDecimal.valueOf(400), currency.currencyCode(), account1CreatedAt);
        final Account targetAccount =
                new Account(2L, BigDecimal.valueOf(300), currency.currencyCode(), account2CreatedAt);

        // ===== WHEN =====
        when(accountService.getAccount(1L)).thenReturn(Mono.just(sourceAccount));
        when(accountService.getAccount(2L)).thenReturn(Mono.just(targetAccount));
        RuntimeException runtimeException = mock(RuntimeException.class);
        when(currencyService.convertAmount(any(BigDecimal.class), any(String.class), any(String.class)))
                .thenReturn(Mono.error(runtimeException));

        // === EXECUTE ====
        StepVerifier.create(transactionApplicationService.applyTransaction(Mono.just(transactionRequestDto)))
                .expectErrorMatches(runtimeException::equals)
                .verify();

        // ===== THEN =====
        verify(accountService).getAccount(1L);
        verify(accountService).getAccount(2L);
        verify(currencyService).convertAmount(BigDecimal.valueOf(5.4),
                sourceAccount.currency(), targetAccount.currency());
    }

    @Test
    void test_applyTransaction_when_targetAccountIsNotFound_ReturnsMonoError() {
        // ===== GIVEN =====
        final TransactionRequestDto transactionRequestDto = new TransactionRequestDto(1L, 2L, BigDecimal.valueOf(5.4));

        final Account sourceAccount =
                new Account(1L, BigDecimal.valueOf(400), currency.currencyCode(), account1CreatedAt);

        // ===== WHEN =====
        when(accountService.getAccount(1L)).thenReturn(Mono.just(sourceAccount));
        when(accountService.getAccount(2L)).thenReturn(Mono.empty());

        // === EXECUTE ====
        StepVerifier.create(transactionApplicationService.applyTransaction(Mono.just(transactionRequestDto)))
                .verifyErrorSatisfies(e -> {
                    assertTrue(e instanceof ValidationException);
                    assertEquals("targetAccountId: must be an existing account id", e.getMessage());
                });

        // ===== THEN =====
        verify(accountService).getAccount(1L);
        verify(accountService).getAccount(2L);
    }

    @Test
    void test_applyTransaction_when_sourceAccountIsNotFound_ReturnsMonoError() {
        // ===== GIVEN =====
        final TransactionRequestDto transactionRequestDto = new TransactionRequestDto(1L, 2L, BigDecimal.valueOf(5.4));

        // ===== WHEN =====
        when(accountService.getAccount(1L)).thenReturn(Mono.empty());

        // === EXECUTE ====
        StepVerifier.create(transactionApplicationService.applyTransaction(Mono.just(transactionRequestDto)))
                .verifyErrorSatisfies(e -> {
                    assertTrue(e instanceof ValidationException);
                    assertEquals("sourceAccountId: must be an existing account id", e.getMessage());
                });

        // ===== THEN =====
        verify(accountService).getAccount(1L);
    }
}