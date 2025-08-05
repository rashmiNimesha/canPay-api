package com.canpay.api.service.dashboard;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.canpay.api.entity.Bus;
import com.canpay.api.entity.Wallet;
import com.canpay.api.entity.Wallet.WalletType;
import com.canpay.api.entity.User;
import com.canpay.api.entity.User.UserRole;
import com.canpay.api.dto.UserWalletBalanceDto;
import com.canpay.api.lib.Utils;
import com.canpay.api.repository.dashboard.DWalletRepository;

/**
 * Service for managing Wallet entities in the dashboard context.
 * Handles wallet creation, management, and operations for users and buses.
 */
@Service
public class DWalletService {

    private final DWalletRepository walletRepository;

    @Autowired
    public DWalletService(DWalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    /**
     * Creates a new wallet for the given user.
     * Generates a unique wallet number automatically.
     */
    @Transactional
    public Wallet createWallet(User user, WalletType type) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Check if user already has a wallet of this type
        Optional<Wallet> existingWallet = walletRepository.findByUser_IdAndType(user.getId(), type);
        if (existingWallet.isPresent()) {
            return existingWallet.get();
        }

        // Create and associate a wallet for the user
        Wallet wallet = new Wallet(user, type);
        wallet.setWalletNumber(Utils.generateUniqueWalletNumber(walletRepository));

        return walletRepository.save(wallet);
    }

    /**
     * Creates a new wallet for the given bus.
     * Generates a unique wallet number automatically.
     */
    @Transactional
    public Wallet createBusWallet(Bus bus) {
        if (bus == null) {
            throw new IllegalArgumentException("Bus cannot be null");
        }

        // Check if bus already has a wallet
        Optional<Wallet> existingWallet = walletRepository.findByBus_Id(bus.getId());
        if (existingWallet.isPresent()) {
            return existingWallet.get();
        }

        // Create and associate a wallet for the bus
        Wallet wallet = new Wallet(bus, Utils.generateUniqueWalletNumber(walletRepository));

        return walletRepository.save(wallet);
    }

    /**
     * Ensures a user has a wallet, creates one if not exists.
     */
    @Transactional
    public Wallet ensureUserWallet(User user, WalletType type) {
        if (user.getWallet() == null || !user.getWallet().getType().equals(type)) {
            Wallet wallet = createWallet(user, type);
            user.setWallet(wallet);
            return wallet;
        }
        return user.getWallet();
    }

    /**
     * Gets a wallet by user ID.
     */
    public Optional<Wallet> getWalletByUserId(UUID userId) {
        return walletRepository.findByUser_Id(userId);
    }

    /**
     * Gets a wallet by bus ID.
     */
    public Optional<Wallet> getWalletByBusId(UUID busId) {
        return walletRepository.findByBus_Id(busId);
    }

    /**
     * Gets a wallet by wallet number.
     */
    public Optional<Wallet> getWalletByNumber(String walletNumber) {
        return walletRepository.findByWalletNumber(walletNumber);
    }

    /**
     * Deletes a wallet by user ID.
     */
    @Transactional
    public void deleteWalletByUserId(UUID userId) {
        walletRepository.deleteByUser_Id(userId);
    }

    /**
     * Deletes a wallet by bus ID.
     */
    @Transactional
    public void deleteWalletByBusId(UUID busId) {
        walletRepository.deleteByBus_Id(busId);
    }

    /**
     * Get wallet details for an owner by ownerId.
     * Throws NoSuchElementException if not found or not an owner.
     */
    public Map<String, Object> getOwnerWalletDetails(UUID ownerId) {
        Optional<User> ownerOpt = walletRepository.findByUser_Id(ownerId)
                .flatMap(wallet -> Optional.ofNullable(wallet.getUser()));
        if (ownerOpt.isEmpty() || !UserRole.OWNER.equals(ownerOpt.get().getRole())) {
            throw new NoSuchElementException("Owner not found");
        }
        User owner = ownerOpt.get();
        Wallet wallet = owner.getWallet();
        if (wallet == null) {
            throw new NoSuchElementException("No wallet found for this owner");
        }
        UserWalletBalanceDto dto = new UserWalletBalanceDto(
                owner.getId().toString(),
                owner.getName(),
                owner.getEmail(),
                wallet.getWalletNumber(),
                wallet.getBalance().doubleValue(),
                wallet.getType().toString()
        );
        return Map.of("walletDetails", dto);
    }
}
