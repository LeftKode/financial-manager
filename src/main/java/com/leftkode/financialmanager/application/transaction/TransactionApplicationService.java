package com.leftkode.financialmanager.application.transaction;

import com.leftkode.financialmanager.model.Transaction;
import com.leftkode.financialmanager.rest.transaction.apply.dto.TransactionRequestDto;
import com.leftkode.financialmanager.rest.transaction.apply.dto.TransactionResponseDto;
import reactor.core.publisher.Mono;

/**
 * An application that is responsible for actions regarding {@link Transaction}s
 */
public interface TransactionApplicationService {

    /**
     * Checks if the given information is valid and creates a {@link Transaction}.
     *
     * @param transactionRequestDto The dto that includes all the needed information regarding the transaction
     * @return A {@link TransactionResponseDto}
     */
    Mono<TransactionResponseDto> applyTransaction(Mono<TransactionRequestDto> transactionRequestDto);
}
