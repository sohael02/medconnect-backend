package com.medconnect.backend.repository;

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
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    Optional<Pharmacy> findByNameIgnoreCase(String name);

    List<Pharmacy> findByIsActiveTrue();

    Page<Pharmacy> findByIsActiveTrue(Pageable pageable);

    List<Pharmacy> findByPinCode(String pinCode);

    @Query("SELECT p FROM Pharmacy p WHERE p.isActive = true AND p.name ILIKE %:name%")
    List<Pharmacy> findByNameContainingIgnoreCase(@Param("name") String name);

    @Query("""
        SELECT p FROM Pharmacy p 
        WHERE p.isActive = true 
        AND p.latitude BETWEEN :minLat AND :maxLat 
        AND p.longitude BETWEEN :minLng AND :maxLng
        """)
    List<Pharmacy> findByBoundingBox(
            @Param("minLat") Double minLat,
            @Param("maxLat") Double maxLat,
            @Param("minLng") Double minLng,
            @Param("maxLng") Double maxLng
    );

    @Query("SELECT COUNT(p) FROM Pharmacy p WHERE p.isActive = true")
    long countActivePharmacies();
}