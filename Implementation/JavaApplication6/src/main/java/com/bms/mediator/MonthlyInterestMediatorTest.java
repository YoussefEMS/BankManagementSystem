package com.bms.mediator.monthlyinterest;

import com.bms.mediator.monthlyinterest.impl.MonthlyInterestMediatorImpl;
import com.bms.mediator.monthlyinterest.peers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Comprehensive Unit Tests for Monthly Interest Mediator
 * 
 * Tests all components working together through mocked peers.
 * Covers success scenarios, failure scenarios, and edge cases.
 * 
 * SOLID: Tests follow Single Responsibility - each test validates ONE behavior
 */
@DisplayName("Monthly Interest Mediator Tests")
class MonthlyInterestMediatorTest {

    @Mock
    private IAccountPeer mockAccountPeer;

    @Mock
    private IInterestCalculatorPeer mockCalculatorPeer;

    @Mock
    private ITransactionFactoryPeer mockTransactionPeer;

    @Mock
    private IAccountUpdaterPeer mockUpdaterPeer;

    @Mock
    private IAuditLoggerPeer mockAuditPeer;

    @Mock
    private INotificationServicePeer mockNotificationPeer;

    private IMonthlyInterestMediator mediator;
    private Date runDate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mediator = new MonthlyInterestMediatorImpl(
                mockAccountPeer,
                mockCalculatorPeer,
                mockTransactionPeer,
                mockUpdaterPeer,
                mockAuditPeer,
                mockNotificationPeer);
        runDate = new Date();
    }

    // ==================== SUCCESSFUL SCENARIOS ====================

    @Nested
    @DisplayName("Successful Interest Posting Scenarios")
    class SuccessfulScenarios {

        @Test
        @DisplayName("Should process single account successfully")
        void testSingleAccountProcessing() {
            // Arrange
            List<String> accounts = Arrays.asList("ACC001");
            when(mockAccountPeer.getActiveAccounts(any())).thenReturn(accounts);
            when(mockAccountPeer.getAccountType("ACC001")).thenReturn("SAVINGS");
            when(mockAccountPeer.getAccountBalance("ACC001")).thenReturn(10000.0);
            when(mockCalculatorPeer.isEligibleForInterest("SAVINGS", null)).thenReturn(true);
            when(mockCalculatorPeer.getMonthlyInterestRate("SAVINGS", null)).thenReturn(0.00375); // 4.5% annual
            when(mockCalculatorPeer.calculateInterest(10000.0, 0.00375, "SAVINGS", null)).thenReturn(37.5);
            when(mockTransactionPeer.validateTransactionData("ACC001", 37.5)).thenReturn(true);
            when(mockTransactionPeer.createInterestTransaction("ACC001", 37.5, 10000.0, 10037.5, null)).thenReturn(1001);
            when(mockUpdaterPeer.updateAccountBalance("ACC001", 10037.5, 37.5, null)).thenReturn(true);
            when(mockUpdaterPeer.recordInterestPostingMetadata("ACC001", 1001, null)).thenReturn(true);

            // Act
            MonthlyInterestContext result = mediator.postMonthlyInterest(runDate);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.getTotalAccountsProcessed());
            assertEquals(1, result.getSuccessfulOperations());
            assertEquals(0, result.getFailedOperations());
            assertEquals(37.5, result.getTotalInterestPosted(), 0.01);

            // Verify peer interactions
            verify(mockAccountPeer).getActiveAccounts(any());
            verify(mockCalculatorPeer).isEligibleForInterest(eq("SAVINGS"), isNull());
            verify(mockTransactionPeer).createInterestTransaction(anyString(), anyDouble(), anyDouble(), anyDouble(), any());
            verify(mockUpdaterPeer).updateAccountBalance(eq("ACC001"), eq(10037.5), eq(37.5), isNull());
            verify(mockAuditPeer).logSuccessfulInterestPosting(eq("ACC001"), eq(37.5), eq(10037.5), isNull());
            verify(mockNotificationPeer).notifyInterestPosting(anyInt(), eq("ACC001"), eq(37.5), eq(10037.5), isNull());
        }

        @Test
        @DisplayName("Should process multiple accounts successfully")
        void testMultipleAccountsProcessing() {
            // Arrange
            List<String> accounts = Arrays.asList("ACC001", "ACC002", "ACC003");
            when(mockAccountPeer.getActiveAccounts(any())).thenReturn(accounts);

            // Setup for ACC001
            when(mockAccountPeer.getAccountType("ACC001")).thenReturn("SAVINGS");
            when(mockAccountPeer.getAccountBalance("ACC001")).thenReturn(10000.0);
            when(mockCalculatorPeer.isEligibleForInterest("SAVINGS", null)).thenReturn(true);
            when(mockCalculatorPeer.getMonthlyInterestRate("SAVINGS", null)).thenReturn(0.00375);
            when(mockCalculatorPeer.calculateInterest(10000.0, 0.00375, "SAVINGS", null)).thenReturn(37.5);

            // Setup for ACC002
            when(mockAccountPeer.getAccountType("ACC002")).thenReturn("MONEY_MARKET");
            when(mockAccountPeer.getAccountBalance("ACC002")).thenReturn(50000.0);
            when(mockCalculatorPeer.isEligibleForInterest("MONEY_MARKET", null)).thenReturn(true);
            when(mockCalculatorPeer.getMonthlyInterestRate("MONEY_MARKET", null)).thenReturn(0.005);
            when(mockCalculatorPeer.calculateInterest(50000.0, 0.005, "MONEY_MARKET", null)).thenReturn(250.0);

            // Setup for ACC003
            when(mockAccountPeer.getAccountType("ACC003")).thenReturn("INVESTMENT");
            when(mockAccountPeer.getAccountBalance("ACC003")).thenReturn(100000.0);
            when(mockCalculatorPeer.isEligibleForInterest("INVESTMENT", null)).thenReturn(true);
            when(mockCalculatorPeer.getMonthlyInterestRate("INVESTMENT", null)).thenReturn(0.00625);
            when(mockCalculatorPeer.calculateInterest(100000.0, 0.00625, "INVESTMENT", null)).thenReturn(625.0);

            // Common setup for all
            when(mockTransactionPeer.validateTransactionData(anyString(), anyDouble())).thenReturn(true);
            when(mockTransactionPeer.createInterestTransaction(anyString(), anyDouble(), anyDouble(), anyDouble(), any()))
                    .thenReturn(1001, 1002, 1003);
            when(mockUpdaterPeer.updateAccountBalance(anyString(), anyDouble(), anyDouble(), any())).thenReturn(true);
            when(mockUpdaterPeer.recordInterestPostingMetadata(anyString(), anyInt(), any())).thenReturn(true);

            // Act
            MonthlyInterestContext result = mediator.postMonthlyInterest(runDate);

            // Assert
            assertEquals(3, result.getTotalAccountsProcessed());
            assertEquals(3, result.getSuccessfulOperations());
            assertEquals(0, result.getFailedOperations());
            assertEquals(912.5, result.getTotalInterestPosted(), 0.01); // 37.5 + 250 + 625

            // Verify interactions for each account
            verify(mockTransactionPeer, times(3)).createInterestTransaction(anyString(), anyDouble(), anyDouble(), anyDouble(), any());
            verify(mockNotificationPeer, times(3)).notifyInterestPosting(anyInt(), anyString(), anyDouble(), anyDouble(), any());
        }

        @Test
        @DisplayName("Should handle maximum balance correctly")
        void testMaximumBalanceProcessing() {
            // Arrange - Test with very high balance
            List<String> accounts = Arrays.asList("ACC005");
            when(mockAccountPeer.getActiveAccounts(any())).thenReturn(accounts);
            when(mockAccountPeer.getAccountType("ACC005")).thenReturn("INVESTMENT");
            when(mockAccountPeer.getAccountBalance("ACC005")).thenReturn(10000000.0); // 10 million
            when(mockCalculatorPeer.isEligibleForInterest("INVESTMENT", null)).thenReturn(true);
            when(mockCalculatorPeer.getMonthlyInterestRate("INVESTMENT", null)).thenReturn(0.00625);
            when(mockCalculatorPeer.calculateInterest(10000000.0, 0.00625, "INVESTMENT", null)).thenReturn(62500.0);
            when(mockTransactionPeer.validateTransactionData("ACC005", 62500.0)).thenReturn(true);
            when(mockTransactionPeer.createInterestTransaction(anyString(), anyDouble(), anyDouble(), anyDouble(), any())).thenReturn(5001);
            when(mockUpdaterPeer.updateAccountBalance(anyString(), anyDouble(), anyDouble(), any())).thenReturn(true);
            when(mockUpdaterPeer.recordInterestPostingMetadata(anyString(), anyInt(), any())).thenReturn(true);

            // Act
            MonthlyInterestContext result = mediator.postMonthlyInterest(runDate);

            // Assert
            assertEquals(1, result.getSuccessfulOperations());
            assertEquals(62500.0, result.getTotalInterestPosted(), 0.01);
        }
    }

    // ==================== FAILURE SCENARIOS ====================

    @Nested
    @DisplayName("Failed Interest Posting Scenarios")
    class FailureScenarios {

        @Test
        @DisplayName("Should handle account ineligible for interest")
        void testIneligibleAccountType() {
            // Arrange - CURRENT accounts don't earn interest
            List<String> accounts = Arrays.asList("ACC002");
            when(mockAccountPeer.getActiveAccounts(any())).thenReturn(accounts);
            when(mockAccountPeer.getAccountType("ACC002")).thenReturn("CURRENT");
            when(mockAccountPeer.getAccountBalance("ACC002")).thenReturn(50000.0);
            when(mockCalculatorPeer.isEligibleForInterest("CURRENT", null)).thenReturn(false);

            // Act
            MonthlyInterestContext result = mediator.postMonthlyInterest(runDate);

            // Assert
            assertEquals(1, result.getTotalAccountsProcessed());
            assertEquals(0, result.getSuccessfulOperations());
            assertEquals(0, result.getFailedOperations()); // Not counted as failed, just skipped

            // Verify no transaction was created
            verify(mockTransactionPeer, never()).createInterestTransaction(anyString(), anyDouble(), anyDouble(), anyDouble(), any());
        }

        @Test
        @DisplayName("Should handle invalid transaction validation")
        void testInvalidTransactionData() {
            // Arrange
            List<String> accounts = Arrays.asList("ACC001");
            when(mockAccountPeer.getActiveAccounts(any())).thenReturn(accounts);
            when(mockAccountPeer.getAccountType("ACC001")).thenReturn("SAVINGS");
            when(mockAccountPeer.getAccountBalance("ACC001")).thenReturn(10000.0);
            when(mockCalculatorPeer.isEligibleForInterest("SAVINGS", null)).thenReturn(true);
            when(mockCalculatorPeer.getMonthlyInterestRate("SAVINGS", null)).thenReturn(0.00375);
            when(mockCalculatorPeer.calculateInterest(10000.0, 0.00375, "SAVINGS", null)).thenReturn(37.5);
            when(mockTransactionPeer.validateTransactionData("ACC001", 37.5)).thenReturn(false); // Validation fails

            // Act
            MonthlyInterestContext result = mediator.postMonthlyInterest(runDate);

            // Assert
            assertEquals(1, result.getTotalAccountsProcessed());
            assertEquals(0, result.getSuccessfulOperations());
            assertEquals(1, result.getFailedOperations());

            // Verify components not called after validation failure
            verify(mockTransactionPeer, never()).createInterestTransaction(anyString(), anyDouble(), anyDouble(), anyDouble(), any());
            verify(mockUpdaterPeer, never()).updateAccountBalance(anyString(), anyDouble(), anyDouble(), any());
        }

        @Test
        @DisplayName("Should handle transaction creation failure")
        void testTransactionCreationFailure() {
            // Arrange
            List<String> accounts = Arrays.asList("ACC001");
            when(mockAccountPeer.getActiveAccounts(any())).thenReturn(accounts);
            when(mockAccountPeer.getAccountType("ACC001")).thenReturn("SAVINGS");
            when(mockAccountPeer.getAccountBalance("ACC001")).thenReturn(10000.0);
            when(mockCalculatorPeer.isEligibleForInterest("SAVINGS", null)).thenReturn(true);
            when(mockCalculatorPeer.getMonthlyInterestRate("SAVINGS", null)).thenReturn(0.00375);
            when(mockCalculatorPeer.calculateInterest(10000.0, 0.00375, "SAVINGS", null)).thenReturn(37.5);
            when(mockTransactionPeer.validateTransactionData("ACC001", 37.5)).thenReturn(true);
            when(mockTransactionPeer.createInterestTransaction(anyString(), anyDouble(), anyDouble(), anyDouble(), any())).thenReturn(-1); // Failure

            // Act
            MonthlyInterestContext result = mediator.postMonthlyInterest(runDate);

            // Assert
            assertEquals(1, result.getTotalAccountsProcessed());
            assertEquals(0, result.getSuccessfulOperations());
            assertEquals(1, result.getFailedOperations());

            // Verify balance not updated when transaction creation fails
            verify(mockUpdaterPeer, never()).updateAccountBalance(anyString(), anyDouble(), anyDouble(), any());
        }

        @Test
        @DisplayName("Should handle balance update failure")
        void testBalanceUpdateFailure() {
            // Arrange
            List<String> accounts = Arrays.asList("ACC001");
            when(mockAccountPeer.getActiveAccounts(any())).thenReturn(accounts);
            when(mockAccountPeer.getAccountType("ACC001")).thenReturn("SAVINGS");
            when(mockAccountPeer.getAccountBalance("ACC001")).thenReturn(10000.0);
            when(mockCalculatorPeer.isEligibleForInterest("SAVINGS", null)).thenReturn(true);
            when(mockCalculatorPeer.getMonthlyInterestRate("SAVINGS", null)).thenReturn(0.00375);
            when(mockCalculatorPeer.calculateInterest(10000.0, 0.00375, "SAVINGS", null)).thenReturn(37.5);
            when(mockTransactionPeer.validateTransactionData("ACC001", 37.5)).thenReturn(true);
            when(mockTransactionPeer.createInterestTransaction(anyString(), anyDouble(), anyDouble(), anyDouble(), any())).thenReturn(1001);
            when(mockUpdaterPeer.updateAccountBalance(anyString(), anyDouble(), anyDouble(), any())).thenReturn(false); // Update fails

            // Act
            MonthlyInterestContext result = mediator.postMonthlyInterest(runDate);

            // Assert
            assertEquals(1, result.getTotalAccountsProcessed());
            assertEquals(0, result.getSuccessfulOperations());
            assertEquals(1, result.getFailedOperations());

            // Verify audit logging for failure
            verify(mockAuditPeer).logFailedInterestPosting(eq("ACC001"), anyString(), any());
        }
    }

    // ==================== EDGE CASES ====================

    @Nested
    @DisplayName("Edge Case Scenarios")
    class EdgeCaseScenarios {

        @Test
        @DisplayName("Should handle empty account list")
        void testEmptyAccountList() {
            // Arrange
            when(mockAccountPeer.getActiveAccounts(any())).thenReturn(Collections.emptyList());

            // Act
            MonthlyInterestContext result = mediator.postMonthlyInterest(runDate);

            // Assert
            assertEquals(0, result.getTotalAccountsProcessed());
            assertEquals(0, result.getSuccessfulOperations());
            assertEquals(0, result.getFailedOperations());
            assertEquals(0.0, result.getTotalInterestPosted());

            // Verify audit logging for empty batch
            verify(mockAuditPeer).logBatchStart(any());
            verify(mockAuditPeer).logBatchCompletion(any());
        }

        @Test
        @DisplayName("Should handle zero interest calculation")
        void testZeroInterestCalculation() {
            // Arrange
            List<String> accounts = Arrays.asList("ACC001");
            when(mockAccountPeer.getActiveAccounts(any())).thenReturn(accounts);
            when(mockAccountPeer.getAccountType("ACC001")).thenReturn("SAVINGS");
            when(mockAccountPeer.getAccountBalance("ACC001")).thenReturn(0.50); // Very small balance
            when(mockCalculatorPeer.isEligibleForInterest("SAVINGS", null)).thenReturn(true);
            when(mockCalculatorPeer.getMonthlyInterestRate("SAVINGS", null)).thenReturn(0.00375);
            when(mockCalculatorPeer.calculateInterest(0.50, 0.00375, "SAVINGS", null)).thenReturn(0.0); // Rounds to 0
            when(mockTransactionPeer.validateTransactionData("ACC001", 0.0)).thenReturn(true);
            when(mockTransactionPeer.createInterestTransaction(anyString(), anyDouble(), anyDouble(), anyDouble(), any())).thenReturn(1001);
            when(mockUpdaterPeer.updateAccountBalance(anyString(), anyDouble(), anyDouble(), any())).thenReturn(true);
            when(mockUpdaterPeer.recordInterestPostingMetadata(anyString(), anyInt(), any())).thenReturn(true);

            // Act
            MonthlyInterestContext result = mediator.postMonthlyInterest(runDate);

            // Assert
            assertEquals(1, result.getSuccessfulOperations());
            assertEquals(0.0, result.getTotalInterestPosted());

            // Verify transaction created even with zero interest
            verify(mockTransactionPeer).createInterestTransaction("ACC001", 0.0, 0.50, 0.50, null);
        }

        @Test
        @DisplayName("Should handle mix of success and failure in batch")
        void testMixedSuccessAndFailure() {
            // Arrange
            List<String> accounts = Arrays.asList("ACC001", "ACC002", "ACC003");
            when(mockAccountPeer.getActiveAccounts(any())).thenReturn(accounts);

            // ACC001 - Success
            when(mockAccountPeer.getAccountType("ACC001")).thenReturn("SAVINGS");
            when(mockAccountPeer.getAccountBalance("ACC001")).thenReturn(10000.0);
            when(mockCalculatorPeer.isEligibleForInterest("SAVINGS", null)).thenReturn(true);
            when(mockCalculatorPeer.getMonthlyInterestRate("SAVINGS", null)).thenReturn(0.00375);
            when(mockCalculatorPeer.calculateInterest(10000.0, 0.00375, "SAVINGS", null)).thenReturn(37.5);

            // ACC002 - Failure (ineligible)
            when(mockAccountPeer.getAccountType("ACC002")).thenReturn("CURRENT");
            when(mockAccountPeer.getAccountBalance("ACC002")).thenReturn(50000.0);
            when(mockCalculatorPeer.isEligibleForInterest("CURRENT", null)).thenReturn(false);

            // ACC003 - Failure (update fails)
            when(mockAccountPeer.getAccountType("ACC003")).thenReturn("SAVINGS");
            when(mockAccountPeer.getAccountBalance("ACC003")).thenReturn(5000.0);
            when(mockCalculatorPeer.isEligibleForInterest("SAVINGS", null)).thenReturn(true);
            when(mockCalculatorPeer.getMonthlyInterestRate("SAVINGS", null)).thenReturn(0.00375);
            when(mockCalculatorPeer.calculateInterest(5000.0, 0.00375, "SAVINGS", null)).thenReturn(18.75);
            when(mockTransactionPeer.validateTransactionData(anyString(), anyDouble())).thenReturn(true);
            when(mockTransactionPeer.createInterestTransaction(anyString(), anyDouble(), anyDouble(), anyDouble(), any()))
                    .thenReturn(1001, 1003);
            when(mockUpdaterPeer.updateAccountBalance("ACC001", 10037.5, 37.5, null)).thenReturn(true);
            when(mockUpdaterPeer.updateAccountBalance("ACC003", 5018.75, 18.75, null)).thenReturn(false);
            when(mockUpdaterPeer.recordInterestPostingMetadata("ACC001", 1001, null)).thenReturn(true);

            // Act
            MonthlyInterestContext result = mediator.postMonthlyInterest(runDate);

            // Assert
            assertEquals(3, result.getTotalAccountsProcessed());
            assertEquals(1, result.getSuccessfulOperations()); // Only ACC001
            assertEquals(1, result.getFailedOperations());     // Only ACC003
            assertEquals(37.5, result.getTotalInterestPosted()); // Only ACC001
        }

        @Test
        @DisplayName("Should verify audit logging is called")
        void testAuditLoggingCalls() {
            // Arrange
            List<String> accounts = Arrays.asList("ACC001");
            when(mockAccountPeer.getActiveAccounts(any())).thenReturn(accounts);
            when(mockAccountPeer.getAccountType("ACC001")).thenReturn("SAVINGS");
            when(mockAccountPeer.getAccountBalance("ACC001")).thenReturn(10000.0);
            when(mockCalculatorPeer.isEligibleForInterest("SAVINGS", null)).thenReturn(true);
            when(mockCalculatorPeer.getMonthlyInterestRate("SAVINGS", null)).thenReturn(0.00375);
            when(mockCalculatorPeer.calculateInterest(10000.0, 0.00375, "SAVINGS", null)).thenReturn(37.5);
            when(mockTransactionPeer.validateTransactionData("ACC001", 37.5)).thenReturn(true);
            when(mockTransactionPeer.createInterestTransaction(anyString(), anyDouble(), anyDouble(), anyDouble(), any())).thenReturn(1001);
            when(mockUpdaterPeer.updateAccountBalance(anyString(), anyDouble(), anyDouble(), any())).thenReturn(true);
            when(mockUpdaterPeer.recordInterestPostingMetadata(anyString(), anyInt(), any())).thenReturn(true);

            // Act
            MonthlyInterestContext result = mediator.postMonthlyInterest(runDate);

            // Assert - Verify audit logging sequence
            verify(mockAuditPeer).logBatchStart(any());
            verify(mockAuditPeer).logSuccessfulInterestPosting("ACC001", 37.5, 10037.5, null);
            verify(mockAuditPeer).logBatchCompletion(any());
            verify(mockNotificationPeer).notifyBatchCompletion(any());
        }

        @Test
        @DisplayName("Should verify notification is sent for each successful account")
        void testNotificationForEachAccount() {
            // Arrange
            List<String> accounts = Arrays.asList("ACC001", "ACC002");
            when(mockAccountPeer.getActiveAccounts(any())).thenReturn(accounts);

            // Setup for both accounts
            when(mockAccountPeer.getAccountType(anyString())).thenReturn("SAVINGS");
            when(mockAccountPeer.getAccountBalance(anyString())).thenReturn(10000.0);
            when(mockCalculatorPeer.isEligibleForInterest("SAVINGS", null)).thenReturn(true);
            when(mockCalculatorPeer.getMonthlyInterestRate("SAVINGS", null)).thenReturn(0.00375);
            when(mockCalculatorPeer.calculateInterest(10000.0, 0.00375, "SAVINGS", null)).thenReturn(37.5);
            when(mockTransactionPeer.validateTransactionData(anyString(), anyDouble())).thenReturn(true);
            when(mockTransactionPeer.createInterestTransaction(anyString(), anyDouble(), anyDouble(), anyDouble(), any()))
                    .thenReturn(1001, 1002);
            when(mockUpdaterPeer.updateAccountBalance(anyString(), anyDouble(), anyDouble(), any())).thenReturn(true);
            when(mockUpdaterPeer.recordInterestPostingMetadata(anyString(), anyInt(), any())).thenReturn(true);

            // Act
            mediator.postMonthlyInterest(runDate);

            // Assert - Verify notification sent for each account
            verify(mockNotificationPeer, times(2)).notifyInterestPosting(anyInt(), anyString(), anyDouble(), anyDouble(), any());
            verify(mockNotificationPeer).notifyBatchCompletion(any());
        }
    }

    // ==================== MEDIATOR COORDINATION TESTS ====================

    @Nested
    @DisplayName("Mediator Coordination Tests")
    class MediatorCoordinationTests {

        @Test
        @DisplayName("Should register and unregister peers")
        void testPeerRegistration() {
            // Act
            mediator.registerPeer("testPeer", new Object());
            mediator.unregisterPeer("testPeer");

            // Assert - Should not throw exception
            assertDoesNotThrow(() -> {
                mediator.registerPeer("validPeer", new Object());
            });
        }

        @Test
        @DisplayName("Should prevent null context from breaking mediator")
        void testNullContextHandling() {
            // Arrange
            when(mockAccountPeer.getActiveAccounts(any())).thenReturn(null);

            // Act & Assert
            assertThrows(Exception.class, () -> {
                mediator.postMonthlyInterest(runDate);
            });
        }
    }
}
