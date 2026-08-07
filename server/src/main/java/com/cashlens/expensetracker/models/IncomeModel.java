package com.cashlens.expensetracker.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "Income")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IncomeModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    // @JsonBackReference
    @JsonIgnore
    private UserModel user;

    @NotNull
    @Positive
    @Column(precision = 15, scale = 2) // 15 digits, 2 after decimal
    // BigDecimal is recommended for money
    private BigDecimal amount;

    @NotBlank
    private String category;

    @NotBlank
    private String icon;

    private String description;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "income_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull
    private LocalDate incomeDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
