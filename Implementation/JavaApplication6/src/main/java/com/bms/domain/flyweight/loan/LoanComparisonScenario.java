package com.bms.domain.flyweight.loan;

/**
 * Extrinsic comparison data for a specific customer quote scenario.
 */
public class LoanComparisonScenario {
    private final LoanProductFlyweight product;
    private final double amount;
    private final int durationMonths;
    private final double annualRate;
    private final double monthlyInstallment;
    private final double totalRepayment;
    private final double totalInterest;

    public LoanComparisonScenario(LoanProductFlyweight product, double amount, int durationMonths,
            double annualRate, double monthlyInstallment, double totalRepayment, double totalInterest) {
        this.product = product;
        this.amount = amount;
        this.durationMonths = durationMonths;
        this.annualRate = annualRate;
        this.monthlyInstallment = monthlyInstallment;
        this.totalRepayment = totalRepayment;
        this.totalInterest = totalInterest;
    }

    public LoanProductFlyweight getProduct() {
        return product;
    }

    public double getAmount() {
        return amount;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public double getAnnualRate() {
        return annualRate;
    }

    public double getMonthlyInstallment() {
        return monthlyInstallment;
    }

    public double getTotalRepayment() {
        return totalRepayment;
    }

    public double getTotalInterest() {
        return totalInterest;
    }
}
