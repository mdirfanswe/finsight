package com.cashlens.expensetracker.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResetPasswordDTO {
    @NotBlank
    private String token;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;
}
