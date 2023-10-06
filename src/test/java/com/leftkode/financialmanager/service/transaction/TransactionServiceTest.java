package com.leftkode.financialmanager.service.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.leftkode.financialmanager.infrastructure.persistence.account.AccountRepository;
import com.leftkode.financialmanager.infrastructure.persistence.transaction.TransactionRepository;
import com.leftkode.financialmanager.model.Transaction;
import jakarta.validation.ValidationException;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.dao.DataAccessResourceFailureException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class TransactionServiceTest {

    @Mock
    public AccountRepository accountRepository;

    @Mock
    public TransactionRepository transactionRepository;

    @InjectMocks
    public TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        reset(accountRepository, transactionRepository);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(accountRepository, transactionRepository);
    }

    @Test
    void test_applyTransaction() {
        // ===== GIVEN =====
        final Transaction transaction = new Transaction("transaction-id", 1L, 2L, BigDecimal.valueOf(5.4), "EUR");
        final BigDecimal creditAmount = BigDecimal.valueOf(.35294);

        // ===== WHEN =====
        when(accountRepository.reduceBalance(any(BigDecimal.class), any(Long.class))).thenReturn(Mono.empty());
        when(accountRepository.addBalance(any(BigDecimal.class), any(Long.class))).thenReturn(Mono.empty());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.just(transaction));

        // === EXECUTE ====
        StepVerifier.create(transactionService.applyTransaction(transaction, creditAmount))
                .expectNext("transaction-id")
                .verifyComplete();

        // ===== THEN =====
        verify(accountRepository).reduceBalance(transaction.amount(), 1L);
        verify(accountRepository).addBalance(creditAmount, 2L);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void test_applyTransaction_when_errorDuringTransactionPersistence_returns_MonoError() {
        // ===== GIVEN =====
        final Transaction transaction = new Transaction("transaction-id", 1L, 2L, BigDecimal.valueOf(5.4), "EUR");
        final BigDecimal creditAmount = BigDecimal.valueOf(.35294);

        // ===== WHEN =====
        when(accountRepository.reduceBalance(any(BigDecimal.class), any(Long.class))).thenReturn(Mono.empty());
        when(accountRepository.addBalance(any(BigDecimal.class), any(Long.class))).thenReturn(Mono.empty());
        RuntimeException runtimeException = mock(RuntimeException.class);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(Mono.error(runtimeException));

        // === EXECUTE ====
        StepVerifier.create(transactionService.applyTransaction(transaction, creditAmount))
                .verifyErrorMatches(runtimeException::equals);

        // ===== THEN =====
        verify(accountRepository).reduceBalance(transaction.amount(), 1L);
        verify(accountRepository).addBalance(creditAmount, 2L);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void test_applyTransaction_when_errorWhileAddingBalanceToTheTargetAccount_returns_MonoError() {
        // ===== GIVEN =====
        final Transaction transaction = new Transaction("transaction-id", 1L, 2L, BigDecimal.valueOf(5.4), "EUR");
        final BigDecimal creditAmount = BigDecimal.valueOf(.35294);

        // ===== WHEN =====
        when(accountRepository.reduceBalance(any(BigDecimal.class), any(Long.class))).thenReturn(Mono.empty());
        RuntimeException runtimeException = mock(RuntimeException.class);
        when(accountRepository.addBalance(any(BigDecimal.class), any(Long.class)))
                .thenReturn(Mono.error(runtimeException));

        // === EXECUTE ====
        StepVerifier.create(transactionService.applyTransaction(transaction, creditAmount))
                .verifyErrorMatches(runtimeException::equals);

        // ===== THEN =====
        verify(accountRepository).reduceBalance(transaction.amount(), 1L);
        verify(accountRepository).addBalance(creditAmount, 2L);
    }

    @Test
    void test_applyTransaction_when_sourceAccountHasInsufficientBalance_returns_MonoError() {
        // ===== GIVEN =====
        final Transaction transaction = new Transaction("transaction-id", 1L, 2L, BigDecimal.valueOf(5.4), "EUR");
        final BigDecimal creditAmount = BigDecimal.valueOf(.35294);

        // ===== WHEN =====
        DataAccessResourceFailureException exception = mock(DataAccessResourceFailureException.class);
        when(exception.getMessage()).thenReturn("Check constraint 'chk_balance_not_negative' is violated.");
        when(accountRepository.reduceBalance(any(BigDecimal.class), any(Long.class)))
                .thenReturn(Mono.error(exception));

        // === EXECUTE ====
        StepVerifier.create(transactionService.applyTransaction(transaction, creditAmount))
                .verifyErrorSatisfies(e -> {
                    assertTrue(e instanceof ValidationException);
                    assertEquals("sourceAccountId: must have sufficient balance", e.getMessage());
                });

        // ===== THEN =====
        verify(accountRepository).reduceBalance(transaction.amount(), 1L);
    }
}