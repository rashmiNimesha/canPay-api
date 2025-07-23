package com.canpay.api.controller.canpayadmin;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.canpay.api.dto.dashboard.transactions.GenericTransactionDto;
import com.canpay.api.dto.dashboard.transactions.PaymentTransactionDto;
import com.canpay.api.dto.dashboard.transactions.RechargeTransactionDto;
import com.canpay.api.dto.dashboard.transactions.WithdrawalTransactionDto;
import com.canpay.api.entity.ResponseEntityBuilder;
import com.canpay.api.entity.Transaction;
import com.canpay.api.entity.Transaction.TransactionStatus;
import com.canpay.api.entity.Transaction.TransactionType;
import com.canpay.api.service.dashboard.DTransactionService;
import com.canpay.api.service.dashboard.DTransactionService.TransactionStatsDto;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

/**
 * REST controller for managing transactions in the CanPay admin dashboard.
 * Provides read-only endpoints for viewing transactions by type with full
 * details.
 */
@RestController
@RequestMapping("/api/v1/canpay-admin")
public class DTransactionController {
        // private static final Logger logger =
        // LoggerFactory.getLogger(DTransactionController.class);

        private final DTransactionService transactionService;

        @Autowired
        public DTransactionController(DTransactionService transactionService) {
                this.transactionService = transactionService;
        }

        // General transaction endpoints
        /**
         * Get all transactions.
         */
        @GetMapping("/transactions")
        public ResponseEntity<?> getAllTransactions() {
                List<GenericTransactionDto> transactions = transactionService.getAllTransactions();
                return new ResponseEntityBuilder.Builder<List<GenericTransactionDto>>()
                                .resultMessage("All transactions retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get transaction by ID.
         */
        @GetMapping("/transactions/{transactionId}")
        public ResponseEntity<?> getTransactionById(@PathVariable UUID transactionId) {
                Transaction transaction = transactionService.getTransactionById(transactionId);
                return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Transaction details retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(Map.of("transaction", transaction))
                                .buildWrapped();
        }

        /**
         * Get transactions by type.
         */
        @GetMapping("/transactions/type/{type}")
        public ResponseEntity<?> getTransactionsByType(@PathVariable TransactionType type) {
                List<Transaction> transactions = transactionService.getTransactionsByType(type);
                return new ResponseEntityBuilder.Builder<List<Transaction>>()
                                .resultMessage("Transactions by type retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get transactions by status.
         */
        @GetMapping("/transactions/status/{status}")
        public ResponseEntity<?> getTransactionsByStatus(@PathVariable TransactionStatus status) {
                List<Transaction> transactions = transactionService.getTransactionsByStatus(status);
                return new ResponseEntityBuilder.Builder<List<Transaction>>()
                                .resultMessage("Transactions by status retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get transaction statistics.
         */
        @GetMapping("/transactions/statistics")
        public ResponseEntity<?> getTransactionStatistics() {
                TransactionStatsDto stats = transactionService.getTransactionStatistics();
                return new ResponseEntityBuilder.Builder<TransactionStatsDto>()
                                .resultMessage("Transaction statistics retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(stats)
                                .buildWrapped();
        }

        /**
         * Get transaction count by type.
         */
        @GetMapping("/transactions/count/type/{type}")
        public ResponseEntity<?> getTransactionCountByType(@PathVariable TransactionType type) {
                long count = transactionService.getTransactionCountByType(type);
                return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Transaction count by type retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(Map.of("type", type.toString(), "count", count))
                                .buildWrapped();
        }

        /**
         * Get transaction sum by type.
         */
        @GetMapping("/transactions/sum/type/{type}")
        public ResponseEntity<?> getTransactionSumByType(@PathVariable TransactionType type) {
                BigDecimal sum = transactionService.getTransactionSumByType(type);
                return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Transaction sum by type retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(Map.of("type", type.toString(), "sum", sum))
                                .buildWrapped();
        }

        // RECHARGE transaction endpoints
        /**
         * Get all recharge transactions with full details.
         */
        @GetMapping("/transactions/recharge")
        public ResponseEntity<?> getRechargeTransactions() {
                List<RechargeTransactionDto> transactions = transactionService.getRechargeTransactionsWithDetails();
                return new ResponseEntityBuilder.Builder<List<RechargeTransactionDto>>()
                                .resultMessage("Recharge transactions retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get recharge transactions by passenger ID.
         */
        @GetMapping("/transactions/recharge/passenger/{passengerId}")
        public ResponseEntity<?> getRechargeTransactionsByPassengerId(@PathVariable UUID passengerId) {
                List<RechargeTransactionDto> transactions = transactionService
                                .getRechargeTransactionsByPassengerId(passengerId);
                return new ResponseEntityBuilder.Builder<List<RechargeTransactionDto>>()
                                .resultMessage("Recharge transactions by passenger retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        // WITHDRAWAL transaction endpoints
        /**
         * Get all withdrawal transactions with full details.
         */
        @GetMapping("/transactions/withdrawal")
        public ResponseEntity<?> getWithdrawalTransactions() {
                List<WithdrawalTransactionDto> transactions = transactionService.getWithdrawalTransactionsWithDetails();
                return new ResponseEntityBuilder.Builder<List<WithdrawalTransactionDto>>()
                                .resultMessage("Withdrawal transactions retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get owner wallet to bank withdrawals.
         */
        @GetMapping("/transactions/withdrawal/owner-to-bank")
        public ResponseEntity<?> getOwnerToBankWithdrawals() {
                List<WithdrawalTransactionDto> transactions = transactionService.getOwnerWalletToBankWithdrawals();
                return new ResponseEntityBuilder.Builder<List<WithdrawalTransactionDto>>()
                                .resultMessage("Owner to bank withdrawals retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get bus wallet to owner wallet withdrawals.
         */
        @GetMapping("/transactions/withdrawal/bus-to-owner")
        public ResponseEntity<?> getBusToOwnerWithdrawals() {
                List<WithdrawalTransactionDto> transactions = transactionService.getBusToOwnerWalletWithdrawals();
                return new ResponseEntityBuilder.Builder<List<WithdrawalTransactionDto>>()
                                .resultMessage("Bus to owner withdrawals retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get withdrawal transactions by owner ID.
         */
        @GetMapping("/transactions/withdrawal/owner/{ownerId}")
        public ResponseEntity<?> getWithdrawalTransactionsByOwnerId(@PathVariable UUID ownerId) {
                List<WithdrawalTransactionDto> transactions = transactionService
                                .getWithdrawalTransactionsByOwnerId(ownerId);
                return new ResponseEntityBuilder.Builder<List<WithdrawalTransactionDto>>()
                                .resultMessage("Withdrawal transactions by owner retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        // PAYMENT transaction endpoints
        /**
         * Get all payment transactions with full details.
         */
        @GetMapping("/transactions/payment")
        public ResponseEntity<?> getPaymentTransactions() {
                List<PaymentTransactionDto> transactions = transactionService.getPaymentTransactionsWithDetails();
                return new ResponseEntityBuilder.Builder<List<PaymentTransactionDto>>()
                                .resultMessage("Payment transactions retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get payment transactions by passenger ID.
         */
        @GetMapping("/transactions/payment/passenger/{passengerId}")
        public ResponseEntity<?> getPaymentTransactionsByPassengerId(@PathVariable UUID passengerId) {
                List<PaymentTransactionDto> transactions = transactionService
                                .getPaymentTransactionsByPassengerId(passengerId);
                return new ResponseEntityBuilder.Builder<List<PaymentTransactionDto>>()
                                .resultMessage("Payment transactions by passenger retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get payment transactions by bus ID.
         */
        @GetMapping("/transactions/payment/bus/{busId}")
        public ResponseEntity<?> getPaymentTransactionsByBusId(@PathVariable UUID busId) {
                List<PaymentTransactionDto> transactions = transactionService.getPaymentTransactionsByBusId(busId);
                return new ResponseEntityBuilder.Builder<List<PaymentTransactionDto>>()
                                .resultMessage("Payment transactions by bus retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get payment transactions by operator ID.
         */
        @GetMapping("/transactions/payment/operator/{operatorId}")
        public ResponseEntity<?> getPaymentTransactionsByOperatorId(@PathVariable UUID operatorId) {
                List<PaymentTransactionDto> transactions = transactionService
                                .getPaymentTransactionsByOperatorId(operatorId);
                return new ResponseEntityBuilder.Builder<List<PaymentTransactionDto>>()
                                .resultMessage("Payment transactions by operator retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get payment transactions by bus owner ID.
         */
        @GetMapping("/transactions/payment/owner/{ownerId}")
        public ResponseEntity<?> getPaymentTransactionsByBusOwnerId(@PathVariable UUID ownerId) {
                List<PaymentTransactionDto> transactions = transactionService
                                .getPaymentTransactionsByBusOwnerId(ownerId);
                return new ResponseEntityBuilder.Builder<List<PaymentTransactionDto>>()
                                .resultMessage("Payment transactions by bus owner retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        // Additional filtering endpoints
        /**
         * Get transactions by passenger ID.
         */
        @GetMapping("/transactions/passenger/{passengerId}")
        public ResponseEntity<?> getTransactionsByPassengerId(@PathVariable UUID passengerId) {
                List<Transaction> transactions = transactionService.getTransactionsByPassengerId(passengerId);
                return new ResponseEntityBuilder.Builder<List<Transaction>>()
                                .resultMessage("Transactions by passenger retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get transactions by bus ID.
         */
        @GetMapping("/transactions/bus/{busId}")
        public ResponseEntity<?> getTransactionsByBusId(@PathVariable UUID busId) {
                List<Transaction> transactions = transactionService.getTransactionsByBusId(busId);
                return new ResponseEntityBuilder.Builder<List<Transaction>>()
                                .resultMessage("Transactions by bus retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get transactions by operator ID.
         */
        @GetMapping("/transactions/operator/{operatorId}")
        public ResponseEntity<?> getTransactionsByOperatorId(@PathVariable UUID operatorId) {
                List<Transaction> transactions = transactionService.getTransactionsByOperatorId(operatorId);
                return new ResponseEntityBuilder.Builder<List<Transaction>>()
                                .resultMessage("Transactions by operator retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get transactions by owner ID.
         */
        @GetMapping("/transactions/owner/{ownerId}")
        public ResponseEntity<?> getTransactionsByOwnerId(@PathVariable UUID ownerId) {
                List<Transaction> transactions = transactionService.getTransactionsByOwnerId(ownerId);
                return new ResponseEntityBuilder.Builder<List<Transaction>>()
                                .resultMessage("Transactions by owner retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get transactions by date range.
         */
        @GetMapping("/transactions/date-range")
        public ResponseEntity<?> getTransactionsByDateRange(
                        @RequestParam("start") LocalDateTime start,
                        @RequestParam("end") LocalDateTime end) {
                List<Transaction> transactions = transactionService.getTransactionsByDateRange(start, end);
                return new ResponseEntityBuilder.Builder<List<Transaction>>()
                                .resultMessage("Transactions by date range retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(transactions)
                                .buildWrapped();
        }

        /**
         * Get transaction counts.
         */
        @GetMapping("/transactions/count")
        public ResponseEntity<?> getTransactionCounts() {
                long totalTransactions = transactionService.getAllTransactions().size();
                long rechargeTransactions = transactionService.getTransactionCountByType(TransactionType.RECHARGE);
                long withdrawalTransactions = transactionService.getTransactionCountByType(TransactionType.WITHDRAWAL);
                long paymentTransactions = transactionService.getTransactionCountByType(TransactionType.PAYMENT);

                return new ResponseEntityBuilder.Builder<Map<String, Object>>()
                                .resultMessage("Transaction counts retrieved successfully")
                                .httpStatus(HttpStatus.OK)
                                .body(Map.of(
                                                "total", totalTransactions,
                                                "recharge", rechargeTransactions,
                                                "withdrawal", withdrawalTransactions,
                                                "payment", paymentTransactions))
                                .buildWrapped();
        }
}
