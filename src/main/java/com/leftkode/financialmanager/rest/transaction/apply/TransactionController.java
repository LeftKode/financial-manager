package com.leftkode.financialmanager.rest.transaction.apply;


import com.leftkode.financialmanager.rest.transaction.apply.dto.TransactionRequestDto;
import com.leftkode.financialmanager.rest.transaction.apply.dto.TransactionResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

/**
 * The controller that holds all the endpoints related to transaction functionalities.
 */
public interface TransactionController {

    Mono<ResponseEntity<TransactionResponseDto>> applyTransaction(
            @Valid @RequestBody Mono<TransactionRequestDto> requestDto);
}
