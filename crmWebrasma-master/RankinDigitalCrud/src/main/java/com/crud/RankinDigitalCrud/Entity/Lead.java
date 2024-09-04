package com.crud.RankinDigitalCrud.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Data
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String email;
    private String telephone;
    private LocalDateTime dateCreation;
    private String source;
    private String entreprise;
    private String tag;
    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String statut = "Nouveau";

    @Column(name = "created_by")
    private String createdBy;

    private String pdfFileName;

    private Long valeurEstimee;

    private String statusLabel = "Normal";

    @PrePersist
    protected void onCreate() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now().withSecond(0).withNano(0);
        }
    }

    public String getFormattedDateCreation() {
        if (dateCreation != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return dateCreation.format(formatter);
        }
        return null;
    }
}
