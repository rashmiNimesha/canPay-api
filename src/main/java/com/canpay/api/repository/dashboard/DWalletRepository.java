package com.canpay.api.repository.dashboard;

import com.canpay.api.entity.User;
import com.canpay.api.entity.Wallet;
import com.canpay.api.entity.Wallet.WalletType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.springframework.lang.NonNull;

@Repository
public interface DWalletRepository extends JpaRepository<Wallet, UUID> {
    /** Find wallet by user ID */
    Optional<Wallet> findByUser_Id(UUID userId);

    Optional<Wallet> findByUser(User user);

    /** Find wallet by bus ID */
    Optional<Wallet> findByBus_Id(UUID busId);

    /** Find wallet by its ID */
    @Override
    @NonNull
    Optional<Wallet> findById(@NonNull UUID id);

    /** Delete wallet by user ID. */
    void deleteByUser_Id(UUID userId);

    /** Delete wallet by bus ID. */
    void deleteByBus_Id(UUID busId);

    /** Find wallet by wallet number. */
    Optional<Wallet> findByWalletNumber(String walletNumber);

    /** Find wallets by type */
    List<Wallet> findByType(WalletType type);

    /** Find wallet by user ID and type */
    Optional<Wallet> findByUser_IdAndType(UUID userId, WalletType type);

    /** Find wallet by bus ID and type */
    Optional<Wallet> findByBus_IdAndType(UUID busId, WalletType type);

    /** Count wallets by type */
    long countByType(WalletType type);

    Optional<Wallet> findByUserAndType(User user, Wallet.WalletType type);

}