package com.canpay.api.repository.dashboard;

import com.canpay.api.entity.PassengerWallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.lang.NonNull;

@Repository
public interface DPassengerWalletRepository extends JpaRepository<PassengerWallet, UUID> {
    /** Find wallet by passenger ID */
    Optional<PassengerWallet> findByPassenger_Id(UUID passengerId);

    /** Find wallet by its ID */
    @Override
    @NonNull
    Optional<PassengerWallet> findById(@NonNull UUID id);

    /** Delete wallet by passenger ID. */
    void deleteByPassenger_Id(UUID passengerId);

    /** Find wallet by wallet number. */
    Optional<PassengerWallet> findByWalletNumber(String walletNumber);
}