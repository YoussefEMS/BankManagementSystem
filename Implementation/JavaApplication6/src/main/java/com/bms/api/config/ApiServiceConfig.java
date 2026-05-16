package com.bms.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bms.service.AccountManagementService;
import com.bms.service.AccountQueryService;
import com.bms.service.CustomerOnboardingWorkflowService;
import com.bms.service.LoanApplicationService;

@Configuration
public class ApiServiceConfig {
    @Bean
    AccountManagementService accountManagementService() {
        return new AccountManagementService();
    }

    @Bean
    AccountQueryService accountQueryService() {
        return new AccountQueryService();
    }

    @Bean
    LoanApplicationService loanApplicationService() {
        return new LoanApplicationService();
    }

    @Bean
    CustomerOnboardingWorkflowService customerOnboardingWorkflowService(
            AccountManagementService accountManagementService,
            LoanApplicationService loanApplicationService) {
        return new CustomerOnboardingWorkflowService(accountManagementService, loanApplicationService);
    }
}
