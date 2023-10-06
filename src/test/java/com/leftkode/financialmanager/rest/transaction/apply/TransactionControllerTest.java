package com.leftkode.financialmanager.rest.transaction.apply;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void test_applyTransaction() {
        webTestClient.post()
                .uri("transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("""
                        {
                            "sourceAccountId": 1,
                            "targetAccountId": 3,
                            "amount": 10
                        }
                        """))
                .exchange()
                .expectStatus()
                .isOk().expectBody();
    }
}