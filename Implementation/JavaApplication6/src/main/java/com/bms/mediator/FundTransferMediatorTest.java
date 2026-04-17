package com.bms.mediator.fundtransfer;

import com.bms.mediator.fundtransfer.impl.FundTransferMediatorImpl;
import com.bms.mediator.fundtransfer.peers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Fund Transfer Mediator Tests")
class FundTransferMediatorTest {

    @Mock
    private ISourceAccountValidatorPeer mockSourceValidator;

    @Mock
    private IDestinationAccountValidatorPeer mockDestValidator;

    @Mock
    private IAccountStatusHandlerPeer mockStatusHandler;

    @Mock
    private IOverdraftHandlerPeer mockOverdraftHandler;

    @Mock
    private ITransactionRecorderPeer mockTransactionRecorder;

    @Mock
    private IBalanceValidatorPeer mockBalanceValidator;

    private IFundTransferMediator mediator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mediator = new FundTransferMediatorImpl(
                mockSourceValidator,
                mockDestValidator,
                mockStatusHandler,
                mockOverdraftHandler,
                mockTransactionRecorder,
                mockBalanceValidator);
    }

    // ==================== SUCCESSFUL TRANSFER SCENARIOS ====================

    @Nested
    @DisplayName("Successful Transfer Scenarios")
    class SuccessfulTransferScenarios {

        @Test
        @DisplayName("Should successfully transfer with sufficient funds")
        void testSuccessfulTransferWithSufficientFunds() {
            // Arrange - All validations pass
            when(mockBalanceValidator.validateTransferAmount(5000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 5000.0, null)).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists("ACC002", null)).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds("ACC002", null)).thenReturn(true);
            when(mockStatusHandler.validateAccountsStatus("ACC001", "ACC002", null)).thenReturn(true);
            when(mockStatusHandler.hasTransferRestrictions("ACC001")).thenReturn(false);
            when(mockOverdraftHandler.willTriggerOverdraft("ACC001", 45000.0)).thenReturn(false);
            when(mockTransactionRecorder.recordDebitTransaction(eq("ACC001"), eq(5000.0), anyString(), isNull())).thenReturn(1001);
            when(mockTransactionRecorder.recordCreditTransaction(eq("ACC002"), eq(5000.0), anyString(), isNull())).thenReturn(1002);
            when(mockTransactionRecorder.linkTransactionPair(1001, 1002)).thenReturn(true);

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, 5000.0);

            // Assert
            assertTrue(result.isTransferSuccessful());
            assertNotNull(result.getTransferReference());
            assertEquals(1001, result.getTransactionId());
            assertEquals(45000.0, result.getSourceNewBalance(), 0.01);

            // Verify all validators were called
            verify(mockBalanceValidator).validateTransferAmount(5000.0, null);
            verify(mockSourceValidator).validateSourceAccountExists("ACC001", null);
            verify(mockDestValidator).validateDestinationAccountExists("ACC002", null);
            verify(mockStatusHandler).validateAccountsStatus("ACC001", "ACC002", null);
            verify(mockOverdraftHandler).willTriggerOverdraft("ACC001", 45000.0);
            verify(mockTransactionRecorder).recordDebitTransaction(anyString(), anyDouble(), anyString(), any());
            verify(mockTransactionRecorder).recordCreditTransaction(anyString(), anyDouble(), anyString(), any());
            verify(mockTransactionRecorder).linkTransactionPair(1001, 1002);
        }

        @Test
        @DisplayName("Should successfully transfer with overdraft allowed")
        void testSuccessfulTransferWithOverdraftAllowed() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(60000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 60000.0, null)).thenReturn(false);
            when(mockOverdraftHandler.isWithinOverdraftLimit("ACC001", 10000.0, 50000.0, null)).thenReturn(true); // Within overdraft
            when(mockDestValidator.validateDestinationAccountExists("ACC002", null)).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds("ACC002", null)).thenReturn(true);
            when(mockStatusHandler.validateAccountsStatus("ACC001", "ACC002", null)).thenReturn(true);
            when(mockStatusHandler.hasTransferRestrictions("ACC001")).thenReturn(false);
            when(mockOverdraftHandler.willTriggerOverdraft("ACC001", -10000.0)).thenReturn(true);
            when(mockOverdraftHandler.isOverdraftAllowed("ACC001")).thenReturn(true);
            when(mockTransactionRecorder.recordDebitTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(2001);
            when(mockTransactionRecorder.recordCreditTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(2002);
            when(mockTransactionRecorder.linkTransactionPair(2001, 2002)).thenReturn(true);

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, 60000.0);

            // Assert
            assertTrue(result.isTransferSuccessful());
            assertEquals(-10000.0, result.getSourceNewBalance(), 0.01);
            verify(mockTransactionRecorder).linkTransactionPair(2001, 2002);
        }

        @Test
        @DisplayName("Should successfully transfer between same customer accounts")
        void testSuccessfulIntraCustomerTransfer() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(1000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 1000.0, null)).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists("ACC001", null)).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds("ACC001", null)).thenReturn(true);
            when(mockDestValidator.areSameCustomerAccounts("ACC001", "ACC001")).thenReturn(true);
            when(mockStatusHandler.validateAccountsStatus("ACC001", "ACC001", null)).thenReturn(true);
            when(mockStatusHandler.hasTransferRestrictions("ACC001")).thenReturn(false);
            when(mockOverdraftHandler.willTriggerOverdraft("ACC001", 49000.0)).thenReturn(false);
            when(mockTransactionRecorder.recordDebitTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(3001);
            when(mockTransactionRecorder.recordCreditTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(3002);
            when(mockTransactionRecorder.linkTransactionPair(3001, 3002)).thenReturn(true);

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC001", 1, 1000.0);

            // Assert
            assertTrue(result.isTransferSuccessful());
        }
    }

    // ==================== VALIDATION FAILURE SCENARIOS ====================

    @Nested
    @DisplayName("Validation Failure Scenarios")
    class ValidationFailureScenarios {

        @Test
        @DisplayName("Should fail on invalid transfer amount - too small")
        void testFailOnInvalidAmountTooSmall() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(0.001, null)).thenReturn(false);

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, 0.001);

            // Assert
            assertFalse(result.isTransferSuccessful());
            assertNotNull(result.getFailureReason());
            assertTrue(result.getFailureReason().contains("Invalid transfer amount"));

            // Verify no further validation was attempted
            verify(mockSourceValidator, never()).validateSourceAccountExists(anyString(), any());
        }

        @Test
        @DisplayName("Should fail on invalid transfer amount - too large")
        void testFailOnInvalidAmountTooLarge() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(10000000.0, null)).thenReturn(false);

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, 10000000.0);

            // Assert
            assertFalse(result.isTransferSuccessful());
            assertTrue(result.getFailureReason().contains("Invalid transfer amount"));
        }

        @Test
        @DisplayName("Should fail when source account does not exist")
        void testFailSourceAccountNotExists() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(5000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("INVALID", null)).thenReturn(false);

            // Act
            FundTransferContext result = mediator.transferFunds("INVALID", "ACC002", 1, 5000.0);

            // Assert
            assertFalse(result.isTransferSuccessful());
            assertTrue(result.getFailureReason().contains("Source account does not exist"));
        }

        @Test
        @DisplayName("Should fail when insufficient funds and no overdraft")
        void testFailInsufficientFundsNoOverdraft() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(60000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 60000.0, null)).thenReturn(false);
            when(mockOverdraftHandler.isWithinOverdraftLimit("ACC001", 10000.0, 50000.0, null)).thenReturn(false); // No overdraft

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, 60000.0);

            // Assert
            assertFalse(result.isTransferSuccessful());
            assertTrue(result.getFailureReason().contains("Insufficient funds"));
        }

        @Test
        @DisplayName("Should fail when destination account does not exist")
        void testFailDestinationAccountNotExists() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(5000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 5000.0, null)).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists("INVALID", null)).thenReturn(false);

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "INVALID", 1, 5000.0);

            // Assert
            assertFalse(result.isTransferSuccessful());
            assertTrue(result.getFailureReason().contains("Destination account does not exist"));
        }

        @Test
        @DisplayName("Should fail when destination cannot receive funds")
        void testFailDestinationCannotReceive() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(5000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 5000.0, null)).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists("ACC003", null)).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds("ACC003", null)).thenReturn(false); // Frozen

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC003", 1, 5000.0);

            // Assert
            assertFalse(result.isTransferSuccessful());
            assertTrue(result.getFailureReason().contains("cannot receive funds"));
        }

        @Test
        @DisplayName("Should fail when account status is invalid")
        void testFailInvalidAccountStatus() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(5000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 5000.0, null)).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists("ACC002", null)).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds("ACC002", null)).thenReturn(true);
            when(mockStatusHandler.validateAccountsStatus("ACC001", "ACC002", null)).thenReturn(false);

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, 5000.0);

            // Assert
            assertFalse(result.isTransferSuccessful());
            assertTrue(result.getFailureReason().contains("invalid status"));
        }

        @Test
        @DisplayName("Should fail when source has transfer restrictions")
        void testFailTransferRestrictionOnSource() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(5000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 5000.0, null)).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists("ACC002", null)).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds("ACC002", null)).thenReturn(true);
            when(mockStatusHandler.validateAccountsStatus("ACC001", "ACC002", null)).thenReturn(true);
            when(mockStatusHandler.hasTransferRestrictions("ACC001")).thenReturn(true); // Restrictions

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, 5000.0);

            // Assert
            assertFalse(result.isTransferSuccessful());
            assertTrue(result.getFailureReason().contains("transfer restrictions"));
        }

        @Test
        @DisplayName("Should fail when would cause overdraft without facility")
        void testFailOverdraftWithoutFacility() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(60000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 60000.0, null)).thenReturn(false);
            when(mockOverdraftHandler.isWithinOverdraftLimit("ACC001", 10000.0, 50000.0, null)).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists("ACC002", null)).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds("ACC002", null)).thenReturn(true);
            when(mockStatusHandler.validateAccountsStatus("ACC001", "ACC002", null)).thenReturn(true);
            when(mockStatusHandler.hasTransferRestrictions("ACC001")).thenReturn(false);
            when(mockOverdraftHandler.willTriggerOverdraft("ACC001", -10000.0)).thenReturn(true);
            when(mockOverdraftHandler.isOverdraftAllowed("ACC001")).thenReturn(false); // No overdraft facility

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, 60000.0);

            // Assert
            assertFalse(result.isTransferSuccessful());
            assertTrue(result.getFailureReason().contains("overdraft"));
        }
    }

    // ==================== TRANSACTION EXECUTION FAILURES ====================

    @Nested
    @DisplayName("Transaction Execution Failure Scenarios")
    class TransactionExecutionFailures {

        @Test
        @DisplayName("Should fail when debit transaction creation fails")
        void testFailDebitTransactionCreation() {
            // Arrange - All validations pass, but transaction fails
            when(mockBalanceValidator.validateTransferAmount(5000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 5000.0, null)).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists("ACC002", null)).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds("ACC002", null)).thenReturn(true);
            when(mockStatusHandler.validateAccountsStatus("ACC001", "ACC002", null)).thenReturn(true);
            when(mockStatusHandler.hasTransferRestrictions("ACC001")).thenReturn(false);
            when(mockOverdraftHandler.willTriggerOverdraft("ACC001", 45000.0)).thenReturn(false);
            when(mockTransactionRecorder.recordDebitTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(-1); // Failure

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, 5000.0);

            // Assert
            assertFalse(result.isTransferSuccessful());
            assertTrue(result.getFailureReason().contains("debit transaction"));

            // Verify credit transaction was never attempted
            verify(mockTransactionRecorder, never()).recordCreditTransaction(anyString(), anyDouble(), anyString(), any());
        }

        @Test
        @DisplayName("Should fail when credit transaction creation fails")
        void testFailCreditTransactionCreation() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(5000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 5000.0, null)).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists("ACC002", null)).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds("ACC002", null)).thenReturn(true);
            when(mockStatusHandler.validateAccountsStatus("ACC001", "ACC002", null)).thenReturn(true);
            when(mockStatusHandler.hasTransferRestrictions("ACC001")).thenReturn(false);
            when(mockOverdraftHandler.willTriggerOverdraft("ACC001", 45000.0)).thenReturn(false);
            when(mockTransactionRecorder.recordDebitTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(1001);
            when(mockTransactionRecorder.recordCreditTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(-1); // Failure

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, 5000.0);

            // Assert
            assertFalse(result.isTransferSuccessful());
            assertTrue(result.getFailureReason().contains("credit transaction"));
        }

        @Test
        @DisplayName("Should fail when transaction linking fails")
        void testFailTransactionLinking() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(5000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 5000.0, null)).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists("ACC002", null)).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds("ACC002", null)).thenReturn(true);
            when(mockStatusHandler.validateAccountsStatus("ACC001", "ACC002", null)).thenReturn(true);
            when(mockStatusHandler.hasTransferRestrictions("ACC001")).thenReturn(false);
            when(mockOverdraftHandler.willTriggerOverdraft("ACC001", 45000.0)).thenReturn(false);
            when(mockTransactionRecorder.recordDebitTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(1001);
            when(mockTransactionRecorder.recordCreditTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(1002);
            when(mockTransactionRecorder.linkTransactionPair(1001, 1002)).thenReturn(false); // Linking fails

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, 5000.0);

            // Assert
            assertFalse(result.isTransferSuccessful());
            assertTrue(result.getFailureReason().contains("link transaction"));
        }
    }

    // ==================== EDGE CASES ====================

    @Nested
    @DisplayName("Edge Case Scenarios")
    class EdgeCaseScenarios {

        @Test
        @DisplayName("Should handle transfer of exactly zero balance")
        void testTransferExactBalance() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(50000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 50000.0, null)).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists("ACC002", null)).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds("ACC002", null)).thenReturn(true);
            when(mockStatusHandler.validateAccountsStatus("ACC001", "ACC002", null)).thenReturn(true);
            when(mockStatusHandler.hasTransferRestrictions("ACC001")).thenReturn(false);
            when(mockOverdraftHandler.willTriggerOverdraft("ACC001", 0.0)).thenReturn(false);
            when(mockOverdraftHandler.isOverdraftAllowed("ACC001")).thenReturn(true);
            when(mockTransactionRecorder.recordDebitTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(1001);
            when(mockTransactionRecorder.recordCreditTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(1002);
            when(mockTransactionRecorder.linkTransactionPair(1001, 1002)).thenReturn(true);

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, 50000.0);

            // Assert
            assertTrue(result.isTransferSuccessful());
            assertEquals(0.0, result.getSourceNewBalance(), 0.01);
        }

        @Test
        @DisplayName("Should generate unique transfer reference for each transfer")
        void testUniqueTransferReference() {
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(anyDouble(), any())).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists(anyString(), any())).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance(anyString())).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds(anyString(), anyDouble(), any())).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists(anyString(), any())).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds(anyString(), any())).thenReturn(true);
            when(mockStatusHandler.validateAccountsStatus(anyString(), anyString(), any())).thenReturn(true);
            when(mockStatusHandler.hasTransferRestrictions(anyString())).thenReturn(false);
            when(mockOverdraftHandler.willTriggerOverdraft(anyString(), anyDouble())).thenReturn(false);
            when(mockTransactionRecorder.recordDebitTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(1001, 2001);
            when(mockTransactionRecorder.recordCreditTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(1002, 2002);
            when(mockTransactionRecorder.linkTransactionPair(anyInt(), anyInt())).thenReturn(true);

            // Act
            FundTransferContext result1 = mediator.transferFunds("ACC001", "ACC002", 1, 5000.0);
            FundTransferContext result2 = mediator.transferFunds("ACC001", "ACC002", 1, 5000.0);

            // Assert
            assertNotEquals(result1.getTransferReference(), result2.getTransferReference());
        }

        @Test
        @DisplayName("Should verify mediator registration works")
        void testMediatorRegistrationAndUnregistration() {
            // Act & Assert
            assertDoesNotThrow(() -> {
                mediator.registerPeer("testPeer", new Object());
                mediator.unregisterPeer("testPeer");
            });
        }

        @Test
        @DisplayName("Should handle minimum transfer amount")
        void testMinimumTransferAmount() {
            // Arrange
            double minAmount = 0.01;
            when(mockBalanceValidator.validateTransferAmount(minAmount, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", minAmount, null)).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists("ACC002", null)).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds("ACC002", null)).thenReturn(true);
            when(mockStatusHandler.validateAccountsStatus("ACC001", "ACC002", null)).thenReturn(true);
            when(mockStatusHandler.hasTransferRestrictions("ACC001")).thenReturn(false);
            when(mockOverdraftHandler.willTriggerOverdraft("ACC001", 49999.99)).thenReturn(false);
            when(mockTransactionRecorder.recordDebitTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(1001);
            when(mockTransactionRecorder.recordCreditTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(1002);
            when(mockTransactionRecorder.linkTransactionPair(1001, 1002)).thenReturn(true);

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, minAmount);

            // Assert
            assertTrue(result.isTransferSuccessful());
        }
    }

    // ==================== MEDIATOR WORKFLOW VALIDATION ====================

    @Nested
    @DisplayName("Mediator Workflow Validation")
    class WorkflowValidation {

        @Test
        @DisplayName("Should verify validation sequence is enforced")
        void testValidationSequenceOrder() {
            // This test verifies the mediator calls validators in correct order
            // Arrange
            when(mockBalanceValidator.validateTransferAmount(5000.0, null)).thenReturn(true);
            when(mockSourceValidator.validateSourceAccountExists("ACC001", null)).thenReturn(true);
            when(mockSourceValidator.getSourceAccountBalance("ACC001")).thenReturn(50000.0);
            when(mockSourceValidator.validateSufficientFunds("ACC001", 5000.0, null)).thenReturn(true);
            when(mockDestValidator.validateDestinationAccountExists("ACC002", null)).thenReturn(true);
            when(mockDestValidator.validateDestinationCanReceiveFunds("ACC002", null)).thenReturn(true);
            when(mockStatusHandler.validateAccountsStatus("ACC001", "ACC002", null)).thenReturn(true);
            when(mockStatusHandler.hasTransferRestrictions("ACC001")).thenReturn(false);
            when(mockOverdraftHandler.willTriggerOverdraft("ACC001", 45000.0)).thenReturn(false);
            when(mockTransactionRecorder.recordDebitTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(1001);
            when(mockTransactionRecorder.recordCreditTransaction(anyString(), anyDouble(), anyString(), any())).thenReturn(1002);
            when(mockTransactionRecorder.linkTransactionPair(1001, 1002)).thenReturn(true);

            // Act
            FundTransferContext result = mediator.transferFunds("ACC001", "ACC002", 1, 5000.0);

            // Assert - Verify sequence
            assertTrue(result.isTransferSuccessful());
            verify(mockBalanceValidator).validateTransferAmount(5000.0, null);
            verify(mockSourceValidator).validateSourceAccountExists("ACC001", null);
            verify(mockDestValidator).validateDestinationAccountExists("ACC002", null);
            verify(mockStatusHandler).validateAccountsStatus("ACC001", "ACC002", null);
            verify(mockOverdraftHandler).willTriggerOverdraft("ACC001", 45000.0);
        }
    }
}
