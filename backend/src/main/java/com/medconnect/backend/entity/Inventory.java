package com.medconnect.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventories", indexes = {
    @Index(name = "idx_inventory_pharmacy_medicine", columnList = "pharmacy_id, medicine_id", unique = true),
    @Index(name = "idx_inventory_availability", columnList = "availability_status"),
    @Index(name = "idx_inventory_last_updated", columnList = "last_updated_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"pharmacy", "medicine"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pharmacy_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_pharmacy"))
    private Pharmacy pharmacy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicine_id", nullable = false, foreignKey = @ForeignKey(name = "fk_inventory_medicine"))
    private Medicine medicine;

    @Column(name = "quantity")
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "availability_status", nullable = false, length = 20)
    @Builder.Default
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.UNVERIFIED;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "currency", length = 3)
    @Builder.Default
    private String currency = "INR";

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @Column(name = "data_source", length = 50)
    @Builder.Default
    private String dataSource = "IMPORT";

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum AvailabilityStatus {
        IN_STOCK,
        OUT_OF_STOCK,
        LIMITED,
        UNVERIFIED,
        DISCONTINUED
    }

    // Helper methods
    @Transient
    public boolean isVerified() {
        return availabilityStatus != AvailabilityStatus.UNVERIFIED;
    }

    @Transient
    public String getAvailabilityDisplay() {
        return switch (availabilityStatus) {
            case IN_STOCK -> "In Stock";
            case OUT_OF_STOCK -> "Out of Stock";
            case LIMITED -> "Limited Stock";
            case UNVERIFIED -> "Availability Unverified";
            case DISCONTINUED -> "Discontinued";
        };
    }
}