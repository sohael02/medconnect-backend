package com.medconnect.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pharmacies", indexes = {
    @Index(name = "idx_pharmacy_name", columnList = "name"),
    @Index(name = "idx_pharmacy_pincode", columnList = "pin_code"),
    @Index(name = "idx_pharmacy_coordinates", columnList = "latitude, longitude")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "inventories")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private Double latitude;

    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private Double longitude;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "website", length = 500)
    private String website;

    @Column(name = "pin_code", length = 10)
    private String pinCode;

    @Column(name = "plus_code", length = 20)
    private String plusCode;

    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "source_data", columnDefinition = "JSON")
    private String sourceData;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "pharmacy", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Inventory> inventories = new ArrayList<>();

    // Helper methods
    public void addInventory(Inventory inventory) {
        inventories.add(inventory);
        inventory.setPharmacy(this);
    }

    public void removeInventory(Inventory inventory) {
        inventories.remove(inventory);
        inventory.setPharmacy(null);
    }
}