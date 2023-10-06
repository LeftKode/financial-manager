package com.leftkode.financialmanager;

import com.leftkode.financialmanager.application.ApplicationConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication()
@Import(ApplicationConfiguration.class)
public class FinancialManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancialManagerApplication.class, args);
    }
}
