package com.medconnect.backend.repository;

import com.medconnect.backend.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findByNormalizedNameIgnoreCase(String normalizedName);

    Optional<Medicine> findByNormalizedNameIgnoreCaseAndStrengthIgnoreCaseAndDosageFormIgnoreCase(
            String normalizedName, String strength, String dosageForm);

    List<Medicine> findByIsActiveTrue();

    Page<Medicine> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT m FROM Medicine m WHERE m.isActive = true AND m.normalizedName ILIKE %:query%")
    List<Medicine> findByNormalizedNameContainingIgnoreCase(@Param("query") String query);

    @Query("""
        SELECT m FROM Medicine m 
        WHERE m.isActive = true 
        AND (m.normalizedName ILIKE %:query% 
             OR m.brandName ILIKE %:query% 
             OR m.genericName ILIKE %:query%)
        """)
    List<Medicine> searchByQuery(@Param("query") String query);

    @Query("""
        SELECT m FROM Medicine m 
        WHERE m.isActive = true 
        AND m.normalizedName ILIKE %:query%
        AND (:strength IS NULL OR m.strength ILIKE %:strength%)
        AND (:dosageForm IS NULL OR m.dosageForm ILIKE %:dosageForm%)
        """)
    List<Medicine> searchWithFilters(
            @Param("query") String query,
            @Param("strength") String strength,
            @Param("dosageForm") String dosageForm
    );

    @Query("SELECT COUNT(m) FROM Medicine m WHERE m.isActive = true")
    long countActiveMedicines();
}