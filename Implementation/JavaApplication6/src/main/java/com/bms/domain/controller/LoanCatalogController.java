package com.bms.domain.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.bms.domain.flyweight.loan.LoanComparisonScenario;
import com.bms.domain.flyweight.loan.LoanProductFlyweight;
import com.bms.domain.flyweight.loan.LoanProductFlyweightFactory;

/**
 * UC: Loan Product Catalog and Comparison.
 * Uses flyweights to share loan product metadata while each quote keeps only
 * scenario-specific state such as amount and duration.
 */
public class LoanCatalogController {

    public Collection<LoanProductFlyweight> getLoanProducts() {
        return LoanProductFlyweightFactory.getAllProducts();
    }

    public List<LoanComparisonScenario> compareProducts(double amount, int durationMonths) {
        List<LoanComparisonScenario> scenarios = new ArrayList<>();
        if (amount <= 0 || durationMonths <= 0 || durationMonths > 360) {
            return scenarios;
        }

        for (LoanProductFlyweight product : getLoanProducts()) {
            double annualRate = product.calculateRate(amount, durationMonths);
            double monthlyInstallment = estimateMonthlyInstallment(amount, annualRate, durationMonths);
            double totalRepayment = monthlyInstallment * durationMonths;
            double totalInterest = totalRepayment - amount;

            scenarios.add(new LoanComparisonScenario(
                    product,
                    amount,
                    durationMonths,
                    annualRate,
                    monthlyInstallment,
                    totalRepayment,
                    totalInterest));
        }

        return scenarios;
    }

    private double estimateMonthlyInstallment(double principal, double annualRatePercent, int durationMonths) {
        double monthlyRate = annualRatePercent / 100.0 / 12.0;
        if (monthlyRate == 0.0) {
            return principal / durationMonths;
        }

        double factor = Math.pow(1 + monthlyRate, durationMonths);
        return principal * monthlyRate * factor / (factor - 1);
    }
}
