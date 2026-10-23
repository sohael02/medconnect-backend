package com.medconnect.backend.repository;

import com.medconnect.backend.entity.Inventory;
import com.medconnect.backend.entity.Medicine;
import com.medconnect.backend.entity.Pharmacy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByPharmacyAndMedicine(Pharmacy pharmacy, Medicine medicine);

    List<Inventory> findByPharmacy(Pharmacy pharmacy);

    List<Inventory> findByMedicine(Medicine medicine);

    Page<Inventory> findByPharmacy(Pharmacy pharmacy, Pageable pageable);

    List<Inventory> findByAvailabilityStatus(Inventory.AvailabilityStatus status);

    @Query("""
        SELECT i FROM Inventory i
        JOIN FETCH i.pharmacy p
        JOIN FETCH i.medicine m
        WHERE i.medicine = :medicine
        AND p.isActive = true
        AND i.availabilityStatus != :excludedStatus
        """)
    List<Inventory> findByMedicineWithPharmacy(
            @Param("medicine") Medicine medicine,
            @Param("excludedStatus") Inventory.AvailabilityStatus excludedStatus
    );

    @Query("""
        SELECT i FROM Inventory i
        JOIN FETCH i.pharmacy p
        JOIN FETCH i.medicine m
        WHERE i.pharmacy = :pharmacy
        AND m.isActive = true
        ORDER BY m.normalizedName ASC
        """)
    List<Inventory> findByPharmacyWithMedicine(@Param("pharmacy") Pharmacy pharmacy);

    @Query("""
        SELECT i FROM Inventory i
        JOIN FETCH i.medicine m
        WHERE i.pharmacy = :pharmacy
        AND m.normalizedName ILIKE %:query%
        ORDER BY m.normalizedName ASC
        """)
    List<Inventory> searchMedicinesInPharmacy(
            @Param("pharmacy") Pharmacy pharmacy,
            @Param("query") String query
    );

    @Query("""
        SELECT DISTINCT i.pharmacy FROM Inventory i
        JOIN FETCH i.pharmacy p
        JOIN FETCH i.medicine m
        WHERE i.medicine = :medicine
        AND p.isActive = true
        AND i.availabilityStatus != :excludedStatus
        """)
    List<Pharmacy> findPharmaciesByMedicine(
            @Param("medicine") Medicine medicine,
            @Param("excludedStatus") Inventory.AvailabilityStatus excludedStatus
    );

    @Query("""
        SELECT i FROM Inventory i
        JOIN FETCH i.pharmacy p
        JOIN FETCH i.medicine m
        WHERE p.id IN :pharmacyIds
        AND m.normalizedName ILIKE %:query%
        AND i.availabilityStatus != :excludedStatus
        ORDER BY p.name ASC
        """)
    List<Inventory> findByPharmacyIdsAndMedicineQuery(
            @Param("pharmacyIds") List<Long> pharmacyIds,
            @Param("query") String query,
            @Param("excludedStatus") Inventory.AvailabilityStatus excludedStatus
    );

    long countByPharmacy(Pharmacy pharmacy);
}