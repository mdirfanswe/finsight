package com.cashlens.expensetracker.models;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "first_name")
    @NotBlank
    private String firstName;

    @Column(name = "last_name")
    @NotBlank
    private String lastName;

    @NotBlank
    @Column(unique = true)
    @Size(min = 3)
    private String username;

    @NotBlank
    @Column(unique = true)
    @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = "Invalid email format")
    // Matches any string with one @ symbol,
    // no spaces, at least one character before and after the @,
    // and at least one dot in the domain part.
    private String email;

    @NotBlank
    // @Pattern(regexp =
    // "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
    // message = "Password must be at least 8 characters long and include uppercase,
    // lowercase, number, and special character")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // ignores the field only in response, but still allows it in
                                                           // request.
    private String password;

    @Column(name = "password_reset_token", unique = true)
    @JsonIgnore
    private String passwordResetToken;

    @Column(name = "password_reset_expiry")
    @JsonIgnore
    private LocalDateTime passwordResetExpiry;

    @NotBlank
    private String currency;

    // private Set<String> roles = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // field is returned in responses but ignored in requests
    private String role;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // @JsonManagedReference
    @JsonIgnore
    private Set<ExpenseModel> expenses = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    // @JsonManagedReference
    @JsonIgnore
    private Set<IncomeModel> incomes = new HashSet<>();
}
