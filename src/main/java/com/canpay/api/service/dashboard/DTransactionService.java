package com.canpay.api.service.dashboard;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.canpay.api.controller.account.PaymentController;
import com.canpay.api.dto.dashboard.transactions.*;
import com.canpay.api.entity.*;
import com.canpay.api.repository.dashboard.*;
import com.canpay.api.service.implementation.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.canpay.api.entity.Transaction.TransactionStatus;
import com.canpay.api.entity.Transaction.TransactionType;

/**
 * Service for managing Transaction entities in the dashboard context.
 * Handles read-only transaction retrieval with DTO mapping for dashboard
 * viewing.
 */
@Service
public class DTransactionService {
    private final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final DTransactionRepository transactionRepository;
    private final DBusRepository busRepository;
    private final DUserRepository userRepository;
    private final DBankAccountRepository bankAccountRepository;
    private final DWalletRepository walletRepository;
    private final UserServiceImpl userService;

    @Autowired
    public DTransactionService(DTransactionRepository transactionRepository, DBusRepository busRepository, DUserRepository userRepository, DBankAccountRepository bankAccountRepository, DWalletRepository walletRepository, UserServiceImpl userService) {
        this.transactionRepository = transactionRepository;
        this.busRepository = busRepository;
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.walletRepository = walletRepository;
        this.userService = userService;
    }

    /**
     * Gets a transaction by ID.
     */
    public Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
    }

    /**
     * Gets all transactions.
     */
    public List<GenericTransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::convertToGenericDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets transactions by type.
     */
    public List<Transaction> getTransactionsByType(TransactionType type) {
        return transactionRepository.findByType(type);
    }

    /**
     * Gets transactions by status.
     */
    public List<Transaction> getTransactionsByStatus(TransactionStatus status) {
        return transactionRepository.findByStatus(status);
    }

    // RECHARGE specific methods with DTO mapping

    /**
     * Gets all recharge transactions with full details as DTOs.
     */
    public List<RechargeTransactionDto> getRechargeTransactionsWithDetails() {
        List<Transaction> transactions = transactionRepository.findRechargeTransactionsWithBankDetails();
        return transactions.stream()
                .map(this::convertToRechargeDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets recharge transactions by passenger ID as DTOs.
     */
    public List<RechargeTransactionDto> getRechargeTransactionsByPassengerId(UUID passengerId) {
        List<Transaction> transactions = transactionRepository.findRechargeTransactionsByPassengerId(passengerId);
        return transactions.stream()
                .map(this::convertToRechargeDto)
                .collect(Collectors.toList());
    }

    // WITHDRAWAL specific methods with DTO mapping

    /**
     * Gets all withdrawal transactions with full details as DTOs.
     */
    public List<WithdrawalTransactionDto> getWithdrawalTransactionsWithDetails() {
        List<Transaction> ownerToBankWithdrawals = transactionRepository.findOwnerWalletToBankWithdrawals();
        List<Transaction> busToOwnerWithdrawals = transactionRepository.findBusToOwnerWalletWithdrawals();

        List<WithdrawalTransactionDto> result = ownerToBankWithdrawals.stream()
                .map(this::convertToWithdrawalDto)
                .collect(Collectors.toList());

        result.addAll(busToOwnerWithdrawals.stream()
                .map(this::convertToWithdrawalDto)
                .collect(Collectors.toList()));

        return result;
    }

    /**
     * Gets owner wallet to bank withdrawals as DTOs.
     */
    public List<WithdrawalTransactionDto> getOwnerWalletToBankWithdrawals() {
        List<Transaction> transactions = transactionRepository.findOwnerWalletToBankWithdrawals();
        return transactions.stream()
                .map(this::convertToWithdrawalDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets bus wallet to owner wallet withdrawals as DTOs.
     */
    public List<WithdrawalTransactionDto> getBusToOwnerWalletWithdrawals() {
        List<Transaction> transactions = transactionRepository.findBusToOwnerWalletWithdrawals();
        return transactions.stream()
                .map(this::convertToWithdrawalDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets withdrawal transactions by owner ID as DTOs.
     */
    public List<WithdrawalTransactionDto> getWithdrawalTransactionsByOwnerId(UUID ownerId) {
        List<Transaction> transactions = transactionRepository.findWithdrawalTransactionsByOwnerId(ownerId);
        return transactions.stream()
                .map(this::convertToWithdrawalDto)
                .collect(Collectors.toList());
    }

    // PAYMENT specific methods with DTO mapping
    /**
     * Gets all payment transactions with full details as DTOs.
     */
    public List<PaymentTransactionDto> getPaymentTransactionsWithDetails() {
        List<Transaction> transactions = transactionRepository.findPaymentTransactionsWithFullDetails();
        return transactions.stream()
                .map(this::convertToPaymentDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets payment transactions by passenger ID as DTOs.
     */
    public List<PaymentTransactionDto> getPaymentTransactionsByPassengerId(UUID passengerId) {
        List<Transaction> transactions = transactionRepository.findByPassenger_Id(passengerId)
                .stream()
                .filter(t -> t.getType() == TransactionType.PAYMENT)
                .collect(Collectors.toList());
        return transactions.stream()
                .map(this::convertToPaymentDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets payment transactions by bus ID as DTOs.
     */
    public List<PaymentTransactionDto> getPaymentTransactionsByBusId(UUID busId) {
        List<Transaction> transactions = transactionRepository.findByBus_Id(busId)
                .stream()
                .filter(t -> t.getType() == TransactionType.PAYMENT)
                .collect(Collectors.toList());
        return transactions.stream()
                .map(this::convertToPaymentDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets payment transactions by operator ID as DTOs.
     */
    public List<PaymentTransactionDto> getPaymentTransactionsByOperatorId(UUID operatorId) {
        List<Transaction> transactions = transactionRepository.findByOperator_Id(operatorId)
                .stream()
                .filter(t -> t.getType() == TransactionType.PAYMENT)
                .collect(Collectors.toList());
        return transactions.stream()
                .map(this::convertToPaymentDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets payment transactions by bus owner ID as DTOs.
     */
    public List<PaymentTransactionDto> getPaymentTransactionsByBusOwnerId(UUID ownerId) {
        List<Transaction> transactions = transactionRepository.findByOwner_Id(ownerId)
                .stream()
                .filter(t -> t.getType() == TransactionType.PAYMENT)
                .collect(Collectors.toList());
        return transactions.stream()
                .map(this::convertToPaymentDto)
                .collect(Collectors.toList());
    }

    // Analytics and statistics methods

    /**
     * Gets transaction statistics.
     */
    public TransactionStatsDto getTransactionStatistics() {
        long totalTransactions = transactionRepository.count();
        long rechargeTransactions = transactionRepository.countByType(TransactionType.RECHARGE);
        long withdrawalTransactions = transactionRepository.countByType(TransactionType.WITHDRAWAL);
        long paymentTransactions = transactionRepository.countByType(TransactionType.PAYMENT);

        long pendingTransactions = transactionRepository.countByStatus(TransactionStatus.PENDING);
        long approvedTransactions = transactionRepository.countByStatus(TransactionStatus.APPROVED);
        long rejectedTransactions = transactionRepository.countByStatus(TransactionStatus.REJECTED);
        long blockedTransactions = transactionRepository.countByStatus(TransactionStatus.BLOCKED);

        BigDecimal rechargeAmount = transactionRepository.sumAmountByType(TransactionType.RECHARGE);
        BigDecimal withdrawalAmount = transactionRepository.sumAmountByType(TransactionType.WITHDRAWAL);
        BigDecimal paymentAmount = transactionRepository.sumAmountByType(TransactionType.PAYMENT);

        // Handle null values from database
        rechargeAmount = rechargeAmount != null ? rechargeAmount : BigDecimal.ZERO;
        withdrawalAmount = withdrawalAmount != null ? withdrawalAmount : BigDecimal.ZERO;
        paymentAmount = paymentAmount != null ? paymentAmount : BigDecimal.ZERO;

        BigDecimal totalAmount = rechargeAmount.add(withdrawalAmount).add(paymentAmount);

        return new TransactionStatsDto(totalTransactions, rechargeTransactions, withdrawalTransactions,
                paymentTransactions, pendingTransactions, approvedTransactions,
                rejectedTransactions, blockedTransactions, totalAmount,
                rechargeAmount, withdrawalAmount, paymentAmount);
    }

    /**
     * Gets transaction count by type.
     */
    public long getTransactionCountByType(TransactionType type) {
        return transactionRepository.countByType(type);
    }

    /**
     * Gets transaction count by status.
     */
    public long getTransactionCountByStatus(TransactionStatus status) {
        return transactionRepository.countByStatus(status);
    }

    /**
     * Gets transaction sum by type.
     */
    public BigDecimal getTransactionSumByType(TransactionType type) {
        BigDecimal sum = transactionRepository.sumAmountByType(type);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    /**
     * Gets transactions by date range.
     */
    public List<Transaction> getTransactionsByDateRange(LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByHappenedAtBetween(start, end);
    }

    /**
     * Gets transactions by passenger ID.
     */
    public List<Transaction> getTransactionsByPassengerId(UUID passengerId) {
        return transactionRepository.findByPassenger_Id(passengerId);
    }

    /**
     * Gets transactions by bus ID.
     */
    public List<Transaction> getTransactionsByBusId(UUID busId) {
        return transactionRepository.findByBus_Id(busId);
    }

    /**
     * Gets transactions by operator ID.
     */
    public List<Transaction> getTransactionsByOperatorId(UUID operatorId) {
        return transactionRepository.findByOperator_Id(operatorId);
    }

    /**
     * Gets transactions by owner ID.
     */
    public List<Transaction> getTransactionsByOwnerId(UUID ownerId) {
        return transactionRepository.findByOwner_Id(ownerId);
    }

    /**
     * Gets all transactions related to an owner: fare payments (for all their buses) and withdrawals.
     */
    public Map<String, Object> getAllOwnerTransactions(UUID ownerId) {
        // Calculate one week ago
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

        // 1. Fare payments for all buses owned by this owner (last 7 days)
        List<PaymentTransactionDto> farePayments = transactionRepository.findPaymentTransactionsByBusOwner(
                userRepository.findById(ownerId).orElseThrow(() -> new IllegalArgumentException("Owner not found"))
        ).stream()
         .map(this::convertToPaymentDto)
         .filter(dto -> dto.getHappenedAt() != null && !dto.getHappenedAt().isBefore(oneWeekAgo))
         .collect(Collectors.toList());

        // 2. Withdrawals done by owner (wallet to bank / wallet to wallet) (last 7 days)
        List<WithdrawalTransactionDto> allWithdrawals = getWithdrawalTransactionsByOwnerId(ownerId).stream()
            .filter(dto -> dto.getHappenedAt() != null && !dto.getHappenedAt().isBefore(oneWeekAgo))
            .collect(Collectors.toList());

        // Split into withdrawals (to bank) and transfers (wallet to wallet)
        List<WithdrawalTransactionDto> withdrawals = allWithdrawals.stream()
                .filter(w -> w.getToBankAccountId() != null)
                .collect(Collectors.toList());

        List<WithdrawalTransactionDto> transfers = allWithdrawals.stream()
                .filter(w -> w.getToBankAccountId() == null && w.getToWalletId() != null)
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("farePayments", farePayments);
        result.put("withdrawals", withdrawals);
        result.put("transfers", transfers);
        return result;
    }

    // DTO Conversion Methods
    /**
     * Converts Transaction entity to RechargeTransactionDto.
     */
    private RechargeTransactionDto convertToRechargeDto(Transaction transaction) {
        if (transaction.getType() != TransactionType.RECHARGE) {
            throw new IllegalArgumentException("Transaction is not a recharge transaction");
        }

        return new RechargeTransactionDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getHappenedAt(),
                transaction.getStatus(),
                transaction.getNote(),
                // Passenger details
                transaction.getPassenger() != null ? transaction.getPassenger().getId() : null,
                transaction.getPassenger() != null ? transaction.getPassenger().getName() : null,
                transaction.getPassenger() != null ? transaction.getPassenger().getEmail() : null,
                // From bank account details
                transaction.getFromBankAccount() != null ? transaction.getFromBankAccount().getId() : null,
                transaction.getFromBankAccount() != null ? transaction.getFromBankAccount().getBankName() : null,
                transaction.getFromBankAccount() != null
                        ? String.valueOf(transaction.getFromBankAccount().getAccountNumber())
                        : null,
                // To wallet details
                transaction.getToWallet() != null ? transaction.getToWallet().getId() : null,
                transaction.getToWallet() != null ? transaction.getToWallet().getWalletNumber() : null,
                transaction.getToWallet() != null ? transaction.getToWallet().getBalance() : null);
    }

    /**
     * Converts Transaction entity to WithdrawalTransactionDto.
     */
    private WithdrawalTransactionDto convertToWithdrawalDto(Transaction transaction) {
        if (transaction.getType() != TransactionType.WITHDRAWAL) {
            throw new IllegalArgumentException("Transaction is not a withdrawal transaction");
        }

        String withdrawalType = transaction.getToBankAccount() != null ? "OWNER_TO_BANK" : "BUS_TO_OWNER";
        String busRoute = transaction.getBus() != null &&
                transaction.getBus().getRouteFrom() != null &&
                transaction.getBus().getRouteTo() != null
                ? transaction.getBus().getRouteFrom() + " - " + transaction.getBus().getRouteTo()
                : null;

        return new WithdrawalTransactionDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getHappenedAt(),
                transaction.getStatus(),
                transaction.getNote(),
                withdrawalType,
                // Owner details
                transaction.getOwner() != null ? transaction.getOwner().getId() : null,
                transaction.getOwner() != null ? transaction.getOwner().getName() : null,
                transaction.getOwner() != null ? transaction.getOwner().getEmail() : null,
                // From wallet details
                transaction.getFromWallet() != null ? transaction.getFromWallet().getId() : null,
                transaction.getFromWallet() != null ? transaction.getFromWallet().getWalletNumber() : null,
                transaction.getFromWallet() != null ? transaction.getFromWallet().getBalance() : null,
                transaction.getFromWallet() != null ? transaction.getFromWallet().getType().toString() : null,
                // To bank account details (for owner to bank)
                transaction.getToBankAccount() != null ? transaction.getToBankAccount().getId() : null,
                transaction.getToBankAccount() != null ? transaction.getToBankAccount().getBankName() : null,
                transaction.getToBankAccount() != null
                        ? String.valueOf(transaction.getToBankAccount().getAccountNumber())
                        : null,
                // To wallet details (for bus to owner)
                transaction.getToWallet() != null ? transaction.getToWallet().getId() : null,
                transaction.getToWallet() != null ? transaction.getToWallet().getWalletNumber() : null,
                transaction.getToWallet() != null ? transaction.getToWallet().getBalance() : null,
                // Bus details
                transaction.getBus() != null ? transaction.getBus().getId() : null,
                transaction.getBus() != null ? transaction.getBus().getBusNumber() : null,
                busRoute);
    }

    /**
     * Converts Transaction entity to PaymentTransactionDto.
     */
    private PaymentTransactionDto convertToPaymentDto(Transaction transaction) {
        if (transaction.getType() != TransactionType.PAYMENT) {
            throw new IllegalArgumentException("Transaction is not a payment transaction");
        }

        String busRoute = transaction.getBus() != null &&
                transaction.getBus().getRouteFrom() != null &&
                transaction.getBus().getRouteTo() != null
                        ? transaction.getBus().getRouteFrom() + " - " + transaction.getBus().getRouteTo()
                        : null;

        return new PaymentTransactionDto(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getHappenedAt(),
                transaction.getStatus(),
                transaction.getNote(),
                // Passenger details
                transaction.getPassenger() != null ? transaction.getPassenger().getId() : null,
                transaction.getPassenger() != null ? transaction.getPassenger().getName() : null,
                transaction.getPassenger() != null ? transaction.getPassenger().getEmail() : null,
                // Operator details
                transaction.getOperator() != null ? transaction.getOperator().getId() : null,
                transaction.getOperator() != null ? transaction.getOperator().getName() : null,
                transaction.getOperator() != null ? transaction.getOperator().getEmail() : null,
                // Owner details
                transaction.getOwner() != null ? transaction.getOwner().getId() : null,
                transaction.getOwner() != null ? transaction.getOwner().getName() : null,
                transaction.getOwner() != null ? transaction.getOwner().getEmail() : null,
                // Bus details
                transaction.getBus() != null ? transaction.getBus().getId() : null,
                transaction.getBus() != null ? transaction.getBus().getBusNumber() : null,
                busRoute,
                transaction.getBus() != null ? transaction.getBus().getType().toString() : null,
                transaction.getBus() != null ? transaction.getBus().getProvince() : null,
                // From wallet details (passenger wallet)
                transaction.getFromWallet() != null ? transaction.getFromWallet().getId() : null,
                transaction.getFromWallet() != null ? transaction.getFromWallet().getWalletNumber() : null,
                transaction.getFromWallet() != null ? transaction.getFromWallet().getBalance() : null,
                // To wallet details (bus wallet)
                transaction.getToWallet() != null ? transaction.getToWallet().getId() : null,
                transaction.getToWallet() != null ? transaction.getToWallet().getWalletNumber() : null,
                transaction.getToWallet() != null ? transaction.getToWallet().getBalance() : null);
    }

    /**
     * Converts Transaction entity to GenericTransactionDto.
     */
    private GenericTransactionDto convertToGenericDto(Transaction transaction) {
        return new GenericTransactionDto(
                transaction.getId(),
                transaction.getType().toString(),
                transaction.getStatus().toString(),
                transaction.getAmount(),
                transaction.getHappenedAt(),
                transaction.getNote(),
                // Passenger details
                transaction.getPassenger() != null ? transaction.getPassenger().getId() : null,
                transaction.getPassenger() != null ? transaction.getPassenger().getName() : null,
                transaction.getPassenger() != null ? transaction.getPassenger().getEmail() : null,
                // Operator details
                transaction.getOperator() != null ? transaction.getOperator().getId() : null,
                transaction.getOperator() != null ? transaction.getOperator().getName() : null,
                transaction.getOperator() != null ? transaction.getOperator().getEmail() : null,
                // Owner details
                transaction.getOwner() != null ? transaction.getOwner().getId() : null,
                transaction.getOwner() != null ? transaction.getOwner().getName() : null,
                transaction.getOwner() != null ? transaction.getOwner().getEmail() : null,
                // Bus details
                transaction.getBus() != null ? transaction.getBus().getId() : null,
                transaction.getBus() != null ? transaction.getBus().getBusNumber() : null,
                transaction.getBus() != null && transaction.getBus().getRouteFrom() != null
                        && transaction.getBus().getRouteTo() != null
                                ? transaction.getBus().getRouteFrom() + " - " + transaction.getBus().getRouteTo()
                                : null,
                // From wallet details
                transaction.getFromWallet() != null ? transaction.getFromWallet().getId() : null,
                transaction.getFromWallet() != null ? transaction.getFromWallet().getWalletNumber() : null,
                // To wallet details
                transaction.getToWallet() != null ? transaction.getToWallet().getId() : null,
                transaction.getToWallet() != null ? transaction.getToWallet().getWalletNumber() : null,
                // Bank account details
                transaction.getFromBankAccount() != null ? transaction.getFromBankAccount().getBankName() : null,
                transaction.getToBankAccount() != null ? transaction.getToBankAccount().getBankName() : null);
    }

    /**
     * Inner class for transaction statistics.
     */
    public static class TransactionStatsDto {
        private long totalTransactions;
        private long rechargeTransactions;
        private long withdrawalTransactions;
        private long paymentTransactions;
        private long pendingTransactions;
        private long approvedTransactions;
        private long rejectedTransactions;
        private long blockedTransactions;
        private BigDecimal totalAmount;
        private BigDecimal rechargeAmount;
        private BigDecimal withdrawalAmount;
        private BigDecimal paymentAmount;

        public TransactionStatsDto(long totalTransactions, long rechargeTransactions,
                long withdrawalTransactions, long paymentTransactions,
                long pendingTransactions, long approvedTransactions,
                long rejectedTransactions, long blockedTransactions,
                BigDecimal totalAmount, BigDecimal rechargeAmount,
                BigDecimal withdrawalAmount, BigDecimal paymentAmount) {
            this.totalTransactions = totalTransactions;
            this.rechargeTransactions = rechargeTransactions;
            this.withdrawalTransactions = withdrawalTransactions;
            this.paymentTransactions = paymentTransactions;
            this.pendingTransactions = pendingTransactions;
            this.approvedTransactions = approvedTransactions;
            this.rejectedTransactions = rejectedTransactions;
            this.blockedTransactions = blockedTransactions;
            this.totalAmount = totalAmount;
            this.rechargeAmount = rechargeAmount;
            this.withdrawalAmount = withdrawalAmount;
            this.paymentAmount = paymentAmount;
        }

        // Getters
        public long getTotalTransactions() {
            return totalTransactions;
        }

        public long getRechargeTransactions() {
            return rechargeTransactions;
        }

        public long getWithdrawalTransactions() {
            return withdrawalTransactions;
        }

        public long getPaymentTransactions() {
            return paymentTransactions;
        }

        public long getPendingTransactions() {
            return pendingTransactions;
        }

        public long getApprovedTransactions() {
            return approvedTransactions;
        }

        public long getRejectedTransactions() {
            return rejectedTransactions;
        }

        public long getBlockedTransactions() {
            return blockedTransactions;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public BigDecimal getRechargeAmount() {
            return rechargeAmount;
        }

        public BigDecimal getWithdrawalAmount() {
            return withdrawalAmount;
        }

        public BigDecimal getPaymentAmount() {
            return paymentAmount;
        }
    }


    public WithdrawalTransactionDto handleWithdraw(UUID ownerId, OwnerWithdrawRequestDto req) {
        User owner = userService.findUserById(ownerId)
                .orElseThrow(() -> {
                    logger.warn("Owner not found: {}", ownerId);
                    return new RuntimeException("Owner not found");
                });
        if (!owner.getRole().equals(User.UserRole.OWNER)) {
            logger.warn("User is not an owner: {}", ownerId);
            throw new IllegalArgumentException("Invalid owner");
        }

        // Validate amount
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        Wallet fromWallet = null;
        Wallet toWallet = null;
        BankAccount toBankAccount = null;

        // 1. Identify source wallet and owner
        if (req.getFromType() == OwnerWithdrawRequestDto.FromType.BUS) {
            Bus bus = busRepository.findByBusNumber(req.getFromId()).orElse(null);
            if (bus == null || bus.getWallet() == null) {
                throw new IllegalArgumentException("Bus or bus wallet not found");
            }
            fromWallet = bus.getWallet();
            owner = bus.getOwner();
        } else if (req.getFromType() == OwnerWithdrawRequestDto.FromType.OWNER) {
            owner = userRepository.findById(UUID.fromString(req.getFromId())).orElse(null);
            if (owner == null || owner.getWallet() == null) {
                throw new IllegalArgumentException("Owner or owner wallet not found");
            }
            fromWallet = owner.getWallet();
        } else {
            throw new IllegalArgumentException("Invalid fromType");
        }

        // 2. Identify destination
        if (req.getToType() == OwnerWithdrawRequestDto.ToType.WALLET) {
            if (owner == null || owner.getWallet() == null) {
                throw new IllegalArgumentException("Owner wallet not found");
            }
            toWallet = owner.getWallet();
        } else if (req.getToType() == OwnerWithdrawRequestDto.ToType.BANK) {
            if (owner == null) {
                throw new IllegalArgumentException("Owner not found");
            }
            Optional<BankAccount> bankcc = bankAccountRepository.findByUserId(owner.getId())
                    .stream()
                    .filter(BankAccount::isDefault)
                    .findFirst();

            UUID bankIdd = bankcc.isPresent() ? bankcc.get().getId() : null;

            if (bankIdd == null) {
                // Try to get default bank account
                Optional<BankAccount> defaultBank = owner.getBankAccounts().stream().filter(BankAccount::isDefault).findFirst();
                if (defaultBank.isEmpty()) {
                    throw new IllegalArgumentException("No bank account specified or default bank account not found");
                }
                toBankAccount = defaultBank.get();
            } else {
                toBankAccount = bankAccountRepository.findById(bankIdd).orElse(null);
                if (toBankAccount == null) {
                    throw new IllegalArgumentException("Bank account not found");
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid toType");
        }

        // 3. Check balance
        if (fromWallet.getBalance().compareTo(req.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // 4. Perform transfer
        fromWallet.setBalance(fromWallet.getBalance().subtract(req.getAmount()));

        if (toWallet != null) {
            toWallet.setBalance(toWallet.getBalance().add(req.getAmount()));
            walletRepository.save(toWallet);
        }
        walletRepository.save(fromWallet);

        // 5. Record transaction
        Transaction tx = new Transaction();
        tx.setAmount(req.getAmount());
        tx.setType(Transaction.TransactionType.WITHDRAWAL);
        tx.setStatus(Transaction.TransactionStatus.APPROVED);
        tx.setBus(fromWallet.getBus()); // Set bus if available
        tx.setFromWallet(fromWallet);
        tx.setOwner(owner);
        if (toWallet != null) tx.setToWallet(toWallet);
        if (toBankAccount != null) tx.setToBankAccount(toBankAccount);
        tx.setNote("Owner withdraw: " + req.getFromType() + "->" + req.getToType());
        tx.setHappenedAt(java.time.LocalDateTime.now());

        transactionRepository.save(tx);

        // Map to WithdrawalTransactionDto
        WithdrawalTransactionDto responseDto = new WithdrawalTransactionDto(
                tx.getId(),
                tx.getAmount(),
                tx.getHappenedAt(),
                tx.getStatus(),
                tx.getNote(),
                tx.getFromWallet() != null ? tx.getFromWallet().getId() : null,
                tx.getFromWallet().getWalletNumber(),
                tx.getToWallet() != null ? tx.getToWallet().getId() : null,
                tx.getToWallet() != null ? tx.getToWallet().getWalletNumber() : null,
                tx.getToBankAccount() != null ? tx.getToBankAccount().getId() : null,
                tx.getToBankAccount() != null ? tx.getToBankAccount().getBankName() : null,
                tx.getToBankAccount() != null ? String.valueOf(tx.getToBankAccount().getAccountNumber()) : null
        );

        return responseDto;
    }

    /**
     * Gets the total number of transactions done today (from 00:00 to now).
     */
    public long getTodayTotalTransactions() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        return transactionRepository.findByHappenedAtBetween(startOfDay, now).size();
    }
}
