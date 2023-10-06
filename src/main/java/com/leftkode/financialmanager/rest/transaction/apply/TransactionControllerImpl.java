package com.leftkode.financialmanager.rest.transaction.apply;

import com.leftkode.financialmanager.application.transaction.TransactionApplicationService;
import com.leftkode.financialmanager.rest.transaction.apply.dto.TransactionRequestDto;
import com.leftkode.financialmanager.rest.transaction.apply.dto.TransactionResponseDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/transactions")
@Slf4j
public class TransactionControllerImpl implements TransactionController {

    private final TransactionApplicationService transactionApplicationService;

    @Autowired
    public TransactionControllerImpl(TransactionApplicationService transactionApplicationService) {
        this.transactionApplicationService = transactionApplicationService;
    }

    @PostMapping
    public Mono<ResponseEntity<TransactionResponseDto>> applyTransaction(
            @Valid @RequestBody Mono<TransactionRequestDto> requestDto) {
        return transactionApplicationService.applyTransaction(requestDto)
                .map(id -> new ResponseEntity<>(id, HttpStatus.OK));
    }
}
