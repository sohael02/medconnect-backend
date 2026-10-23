package com.medconnect.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medicines", indexes = {
    @Index(name = "idx_medicine_normalized_name", columnList = "normalized_name"),
    @Index(name = "idx_medicine_brand_name", columnList = "brand_name"),
    @Index(name = "idx_medicine_strength_form", columnList = "strength, dosage_form")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "inventories")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "normalized_name", nullable = false, length = 255)
    private String normalizedName;

    @Column(name = "brand_name", length = 255)
    private String brandName;

    @Column(name = "strength", length = 100)
    private String strength;

    @Column(name = "dosage_form", length = 50)
    private String dosageForm;

    @Column(name = "generic_name", length = 255)
    private String genericName;

    @Column(name = "manufacturer", length = 255)
    private String manufacturer;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "medicine", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Inventory> inventories = new ArrayList<>();

    // Computed property for display
    @Transient
    public String getDisplayName() {
        StringBuilder sb = new StringBuilder(normalizedName);
        if (brandName != null && !brandName.isBlank()) {
            sb.append(" (").append(brandName).append(")");
        }
        if (strength != null && !strength.isBlank()) {
            sb.append(" ").append(strength);
        }
        if (dosageForm != null && !dosageForm.isBlank()) {
            sb.append(" ").append(dosageForm);
        }
        return sb.toString();
    }

    // Helper method for matching
    @Transient
    public boolean matchesQuery(String query) {
        if (query == null || query.isBlank()) return false;
        String lowerQuery = query.toLowerCase().trim();
        
        // Check normalized name
        if (normalizedName != null && normalizedName.toLowerCase().contains(lowerQuery)) {
            return true;
        }
        // Check brand name
        if (brandName != null && brandName.toLowerCase().contains(lowerQuery)) {
            return true;
        }
        // Check generic name
        if (genericName != null && genericName.toLowerCase().contains(lowerQuery)) {
            return true;
        }
        return false;
    }
}