package net.javaguides.springboot.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class SaveCTokenRequest {
    @NotBlank(message = "User email is required")
    @Size(max = 255, message = "User email must not exceed 255 characters")
    private String email;

    @NotBlank(message = "CToken is required")
    private String ctoken;
}