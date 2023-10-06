package com.leftkode.financialmanager.service.currency;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.leftkode.financialmanager.infrastructure.persistence.currency.CurrencyRepository;
import com.leftkode.financialmanager.model.Currency;
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
class CurrencyServiceTest {

    @Mock
    public CurrencyRepository currencyRepository;

    @InjectMocks
    public CurrencyServiceImpl currencyService;

    @BeforeEach
    void setUp() {
        reset(currencyRepository);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(currencyRepository);
    }

    @Test
    void test_convertAmount_when_currenciesAreDifferent_returns_convertedAmount() {
        // ===== GIVEN =====
        final LocalDateTime sourceCreatedAt = LocalDateTime.of(2023, 9, 13, 11, 12, 00);
        final LocalDateTime sourceUpdatedAt = LocalDateTime.of(2023, 9, 15, 9, 5, 30);

        final LocalDateTime targetCreatedAt = LocalDateTime.of(2023, 9, 16, 11, 12, 00);
        final LocalDateTime targetUpdatedAt = LocalDateTime.of(2023, 9, 17, 9, 5, 30);

        final Currency sourceCurrency =
                new Currency("EUR", "Euro",
                        BigDecimal.valueOf(0.85000), sourceCreatedAt, sourceUpdatedAt);
        final Currency targetCurrency =
                new Currency("USD", "Euro",
                        BigDecimal.ONE, targetCreatedAt, targetUpdatedAt);

        // ===== WHEN =====
        when(currencyRepository.findById("EUR")).thenReturn(Mono.just(sourceCurrency));
        when(currencyRepository.findById("USD")).thenReturn(Mono.just(targetCurrency));

        // === EXECUTE ====
        StepVerifier.create(currencyService.convertAmount(BigDecimal.valueOf(5.4), "EUR", "USD"))
                .expectNext(BigDecimal.valueOf(6.35294))
                .verifyComplete();

        // ===== THEN =====
        verify(currencyRepository).findById("EUR");
        verify(currencyRepository).findById("USD");
    }

    @Test
    void test_convertAmount_when_exceptionOccursDuringRetrievalOfTargetCurrency_returns_monoError() {
        // ===== GIVEN =====
        final LocalDateTime sourceCreatedAt = LocalDateTime.of(2023, 9, 13, 11, 12, 00);
        final LocalDateTime sourceUpdatedAt = LocalDateTime.of(2023, 9, 15, 9, 5, 30);

        final Currency sourceCurrency =
                new Currency("EUR", "Euro",
                        BigDecimal.valueOf(0.85000), sourceCreatedAt, sourceUpdatedAt);

        // ===== WHEN =====
        when(currencyRepository.findById("EUR")).thenReturn(Mono.just(sourceCurrency));
        RuntimeException runtimeException = mock(RuntimeException.class);
        when(currencyRepository.findById("USD")).thenReturn(Mono.error(runtimeException));

        // === EXECUTE ====
        StepVerifier.create(currencyService.convertAmount(BigDecimal.valueOf(5.4), "EUR", "USD"))
                .expectErrorSatisfies(runtimeException::equals)
                .verify();

        // ===== THEN =====
        verify(currencyRepository).findById("EUR");
        verify(currencyRepository).findById("USD");
    }

    @Test
    void test_convertAmount_when_exceptionOccursDuringRetrievalOfSourceCurrency_returns_monoError() {
        // ===== WHEN =====
        RuntimeException runtimeException = mock(RuntimeException.class);
        when(currencyRepository.findById("EUR")).thenReturn(Mono.error(runtimeException));

        // === EXECUTE ====
        StepVerifier.create(currencyService.convertAmount(BigDecimal.valueOf(5.4), "EUR", "USD"))
                .expectErrorSatisfies(runtimeException::equals)
                .verify();

        // ===== THEN =====
        verify(currencyRepository).findById("EUR");
        verify(currencyRepository).findById("USD");
    }

    @Test
    void test_convertAmount_when_currenciesHaveSameValue_returns_monoOfTheInitialAmount() {
        StepVerifier.create(currencyService.convertAmount(BigDecimal.valueOf(5.4), "USD", "USD"))
                .expectNext(BigDecimal.valueOf(5.4))
                .verifyComplete();
    }
}