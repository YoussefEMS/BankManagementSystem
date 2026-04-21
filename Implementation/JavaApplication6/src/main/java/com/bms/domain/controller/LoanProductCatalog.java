package com.bms.domain.controller;


import com.bms.domain.entity.LoanProductTemplate;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import com.bms.domain.controller.AutoLoanInterestCalculator;
import com.bms.domain.controller.HomeLoanInterestCalculator;
import com.bms.domain.controller.PersonalLoanInterestCalculator;

/**
 * Provides cached shared loan-product flyweights.
 */
public final class LoanProductCatalog {
    private static final Map<String, LoanProductTemplate> PRODUCTS = new LinkedHashMap<>();

    static {
        register(new LoanProductTemplate(
                "PERSONAL",
                "Personal Loan",
                "Flexible unsecured financing for education, travel, medical, or personal expenses.",
                "Fixed 15.0% annual rate",
                "Best for short-to-medium terms when you do not want to pledge collateral.",
                new PersonalLoanInterestCalculator()));

        register(new LoanProductTemplate(
                "HOME",
                "Home Loan",
                "Lower-rate long-term financing designed for property purchase or renovation.",
                "Fixed 5.0% annual rate",
                "Suitable for property-backed borrowing with longer repayment windows.",
                new HomeLoanInterestCalculator()));

        register(new LoanProductTemplate(
                "AUTO",
                "Auto Loan",
                "Vehicle financing with moderate pricing and predictable installment planning.",
                "Fixed 8.0% annual rate",
                "Suitable for customers financing a car purchase with the vehicle as security.",
                new AutoLoanInterestCalculator()));
    }

    private LoanProductCatalog() {
    }

    private static void register(LoanProductTemplate product) {
        PRODUCTS.put(product.getLoanType(), product);
    }

    public static LoanProductTemplate getProduct(String loanType) {
        if (loanType == null) {
            return null;
        }
        return PRODUCTS.get(loanType.trim().toUpperCase());
    }

    public static Collection<LoanProductTemplate> getAllProducts() {
        return PRODUCTS.values();
    }
}
