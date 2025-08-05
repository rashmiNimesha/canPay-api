package com.canpay.api.repository.dashboard;

import com.canpay.api.entity.Bus;
import com.canpay.api.entity.Bus.BusStatus;
import com.canpay.api.entity.Bus.BusType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * Dashboard repository for managing Bus entities.
 * Provides query methods for retrieving and counting buses.
 */
@Repository
public interface DBusRepository extends JpaRepository<Bus, UUID> {
    /** Find bus by its UUID. */
    @Override
    @NonNull
    Optional<Bus> findById(@NonNull UUID id);

    /** Find bus by exact bus number */
    Optional<Bus> findByBusNumber(String busNumber);

    /** Find buses by type */
    List<Bus> findByType(BusType type);

    /** Find buses by status */
    List<Bus> findByStatus(BusStatus status);

    /** Find buses by owner ID */
    List<Bus> findByOwner_Id(UUID ownerId);

    /** Find buses by route from */
    List<Bus> findByRouteFrom(String routeFrom);

    /** Find buses by route to */
    List<Bus> findByRouteTo(String routeTo);

    /** Find buses by province */
    List<Bus> findByProvince(String province);

    /** Find buses created within a date range */
    List<Bus> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /** Find buses updated within a date range */
    List<Bus> findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);

    /** Find bus by wallet ID */
    Optional<Bus> findByWallet_Id(UUID walletId);

    /** Count buses by status */
    long countByStatus(BusStatus status);

    /** Count buses by type */
    long countByType(BusType type);

    /** Count buses by owner ID */
    long countByOwner_Id(UUID ownerId);

    /** Count buses by status and type */
    long countByStatusAndType(BusStatus status, BusType type);

    /** Find buses by bus number containing a substring */
    List<Bus> findByBusNumberContaining(String busNumber);

    /** Find buses by route from containing a substring */
    List<Bus> findByRouteFromContaining(String routeFrom);

    /** Find buses by route to containing a substring */
    List<Bus> findByRouteToContaining(String routeTo);

    /** Find buses by province containing a substring */
    List<Bus> findByProvinceContaining(String province);

    /** Find buses by multiple types */
    List<Bus> findByTypeIn(List<BusType> types);

    /** Find buses by multiple statuses */
    List<Bus> findByStatusIn(List<BusStatus> statuses);

    /** Find buses by status and type */
    List<Bus> findByStatusAndType(BusStatus status, BusType type);

    /** Find buses by owner ID and status */
    List<Bus> findByOwner_IdAndStatus(UUID ownerId, BusStatus status);

    /** Find buses by owner ID and type */
    List<Bus> findByOwner_IdAndType(UUID ownerId, BusType type);

    /** Delete bus by ID */
    void deleteById(@NonNull UUID id);

    /** Delete buses by owner ID */
    void deleteByOwner_Id(UUID ownerId);

    /** Find buses by owner ID */
    List<Bus> findByOwnerId(UUID ownerId);

    /** Count buses by status for a specific owner */
    @Query("SELECT b.status, COUNT(b) FROM Bus b WHERE b.owner.id = :ownerId GROUP BY b.status")
    List<Object[]> countBusesByStatusForOwner(UUID ownerId);

}
