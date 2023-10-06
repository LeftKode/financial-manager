package com.leftkode.financialmanager.infrastructure;

import com.leftkode.financialmanager.infrastructure.persistence.DatabaseConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DatabaseConfiguration.class)
public class InfrastructureConfiguration {

}
