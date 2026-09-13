package com.aeroferia.api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Column(name = "logo_url", nullable = false, length = 300)
    private String logoUrl;

    @Column(name = "banner_url", length = 300)
    private String bannerUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "brands_represented", columnDefinition = "TEXT")
    private String brandsRepresented;

    @Column(name = "ships_nationwide", nullable = false)
    @Builder.Default
    private Boolean shipsNationwide = true;

    @Column(name = "address_line", length = 200)
    private String addressLine;

    @Column(name = "location_province", nullable = false, length = 100)
    private String locationProvince;

    @Column(name = "location_city", nullable = false, length = 100)
    private String locationCity;

    @Column(name = "whatsapp_number", nullable = false, length = 30)
    private String whatsappNumber;

    @Column(name = "website_url", length = 255)
    private String websiteUrl;

    @Column(name = "instagram_handle", length = 100)
    private String instagramHandle;

    @Column(name = "is_verified", nullable = false)
    @Builder.Default
    private Boolean isVerified = true;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Publication> publications = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
