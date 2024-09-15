package com.crud.RankinDigitalCrud.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "notification_leads", joinColumns = @JoinColumn(name = "notification_id"))
    @Column(name = "lead_id")
    private List<Long> leadIds;

    private String subject;
    private String message;
    private LocalDateTime sentAt;
    private String sentBy;
}
