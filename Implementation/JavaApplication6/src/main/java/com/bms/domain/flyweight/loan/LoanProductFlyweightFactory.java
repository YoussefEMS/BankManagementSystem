package com.bms.domain.flyweight.loan;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.bms.domain.controller.AutoLoanInterestCalculator;
import com.bms.domain.controller.HomeLoanInterestCalculator;
import com.bms.domain.controller.PersonalLoanInterestCalculator;

/**
 * Provides cached shared loan-product flyweights.
 */
public final class LoanProductFlyweightFactory {
    private static final Map<String, LoanProductFlyweight> PRODUCTS = new LinkedHashMap<>();

    static {
        register(new LoanProductFlyweight(
                "PERSONAL",
                "Personal Loan",
                "Flexible unsecured financing for education, travel, medical, or personal expenses.",
                "Fixed 15.0% annual rate",
                "Best for short-to-medium terms when you do not want to pledge collateral.",
                new PersonalLoanInterestCalculator()));

        register(new LoanProductFlyweight(
                "HOME",
                "Home Loan",
                "Lower-rate long-term financing designed for property purchase or renovation.",
                "Fixed 5.0% annual rate",
                "Suitable for property-backed borrowing with longer repayment windows.",
                new HomeLoanInterestCalculator()));

        register(new LoanProductFlyweight(
                "AUTO",
                "Auto Loan",
                "Vehicle financing with moderate pricing and predictable installment planning.",
                "Fixed 8.0% annual rate",
                "Suitable for customers financing a car purchase with the vehicle as security.",
                new AutoLoanInterestCalculator()));
    }

    private LoanProductFlyweightFactory() {
    }

    private static void register(LoanProductFlyweight product) {
        PRODUCTS.put(product.getLoanType(), product);
    }

    public static LoanProductFlyweight getProduct(String loanType) {
        if (loanType == null) {
            return null;
        }
        return PRODUCTS.get(loanType.trim().toUpperCase());
    }

    public static Collection<LoanProductFlyweight> getAllProducts() {
        return PRODUCTS.values();
    }
}
