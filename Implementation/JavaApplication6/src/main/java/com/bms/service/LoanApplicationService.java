package com.bms.service;

import java.math.BigDecimal;
import java.util.List;

import com.bms.domain.controller.LoanDecisionService;
import com.bms.domain.controller.LoanStatusController;
import com.bms.domain.entity.Loan;
import com.bms.event.LoanApplicationSubmittedEvent;
import com.bms.event.LoanApprovedEvent;
import com.bms.event.LoanRejectedEvent;
import com.bms.event.bus.EventDispatcher;
import com.bms.persistence.ConfiguredPersistenceProvider;
import com.bms.persistence.PersistenceProvider;
import com.bms.service.base.ApplicationService;

public class LoanApplicationService extends ApplicationService {
    private final com.bms.domain.controller.LoanApplicationService loanApplicationController;
    private final LoanDecisionService loanDecisionService;
    private final LoanStatusController loanStatusController;

    public static class LoanResult {
        private final int loanId;
        private final String status;

        public LoanResult(int loanId, String status) {
            this.loanId = loanId;
            this.status = status;
        }

        public int getLoanId() {
            return loanId;
        }

        public String getStatus() {
            return status;
        }
    }

    public LoanApplicationService() {
        this(ConfiguredPersistenceProvider.getInstance());
    }

    public LoanApplicationService(PersistenceProvider factory) {
        super();
        this.loanApplicationController = new com.bms.domain.controller.LoanApplicationService(factory);
        this.loanDecisionService = new LoanDecisionService(factory);
        this.loanStatusController = new LoanStatusController(factory);
    }

    public LoanApplicationService(
            com.bms.domain.controller.LoanApplicationService loanApplicationController,
            LoanDecisionService loanDecisionService,
            LoanStatusController loanStatusController,
            EventDispatcher eventDispatcher) {
        super(eventDispatcher);
        this.loanApplicationController = loanApplicationController;
        this.loanDecisionService = loanDecisionService;
        this.loanStatusController = loanStatusController;
    }

    public LoanResult applyForLoan(int customerId, double amount, String loanType,
            int durationMonths, String purpose) {
        if (!Double.isFinite(amount)) {
            return new LoanResult(-1, null);
        }

        com.bms.domain.controller.LoanApplicationService.LoanResult result =
                loanApplicationController.applyForLoan(customerId, amount, loanType, durationMonths, purpose);

        if (result == null) {
            return new LoanResult(-1, null);
        }

        LoanResult serviceResult = new LoanResult(result.getLoanId(), result.getStatus());
        if (serviceResult.getLoanId() > 0) {
            publish(new LoanApplicationSubmittedEvent(serviceResult.getLoanId(), customerId,
                    BigDecimal.valueOf(amount), loanType, durationMonths, serviceResult.getStatus()));
        }
        return serviceResult;
    }

    public List<Loan> getPendingLoans() {
        return loanDecisionService.getPendingLoans();
    }

    public Loan getLoanDetails(int loanId) {
        return loanDecisionService.getLoanDetails(loanId);
    }

    public boolean decideLoan(int loanId, String decision) {
        return decideLoan(loanId, decision, -1);
    }

    public boolean decideLoan(int loanId, String decision, int adminId) {
        Loan loan = loanDecisionService.getLoanDetails(loanId);
        boolean updated = loanDecisionService.decideLoan(loanId, decision);
        if (updated && loan != null) {
            if ("APPROVED".equals(decision)) {
                publish(new LoanApprovedEvent(loanId, loan.getCustomerId(), adminId));
            } else if ("REJECTED".equals(decision)) {
                publish(new LoanRejectedEvent(loanId, loan.getCustomerId(), adminId));
            }
        }
        return updated;
    }

    public List<Loan> getLoansForCustomer(int customerId, String filter) {
        return loanStatusController.getLoansForCustomer(customerId, filter);
    }
}
