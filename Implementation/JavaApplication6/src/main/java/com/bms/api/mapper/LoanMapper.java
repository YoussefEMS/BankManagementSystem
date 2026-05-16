package com.bms.api.mapper;

import java.math.BigDecimal;

import com.bms.api.dto.LoanResponse;
import com.bms.domain.entity.Loan;

public final class LoanMapper {
    private LoanMapper() {
    }

    public static LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getLoanId(),
                loan.getCustomerId(),
                BigDecimal.valueOf(loan.getAmount()),
                loan.getLoanType(),
                loan.getStatus(),
                loan.getDurationMonths(),
                loan.getPurpose());
    }
}
