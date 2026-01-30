package com.cafeAurora.model;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.cafeAurora.enums.ReservationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_reservations", 
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"id_table", "reservation_date", "reservation_time"}
       ))
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private Integer idReservation;
    
    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "id_table")
    private TableCoffe table;
    
    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate;
    
    @Column(name = "reservation_time", nullable = false)
    private LocalTime reservationTime;
    
    @Column(name = "num_people", nullable = false)
    private Integer numPeople;
    
    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;
    
    @Column(name = "customer_phone", nullable = false, length = 15)
    private String customerPhone;
    
    @Column(name = "customer_email", nullable = false, length = 100)
    private String customerEmail;
    
    @Column(name = "special_notes", columnDefinition = "TEXT")
    private String specialNotes;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReservationStatus status = ReservationStatus.PENDIENTE;
    
    @ManyToOne
    @JoinColumn(name = "attended_by")
    private User attendedBy;
    
    @Column(name = "response_notes", columnDefinition = "TEXT")
    private String responseNotes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
