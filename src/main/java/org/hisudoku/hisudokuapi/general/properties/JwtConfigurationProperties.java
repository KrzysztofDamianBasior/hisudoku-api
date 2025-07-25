package org.hisudoku.hisudokuapi.general.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

// @Configuration- decorator not necessary as we decorated the main class with @ConfigurationPropertiesScan

// It is standard practice to throw an exception during application startup if required environment variables haven't been provided or if they don't meet certain validation rules
@Validated
@ConfigurationProperties(prefix = "application.security.jwt")
@Data // configuration properties needs getters and setters
public class JwtConfigurationProperties {
    @NotBlank
    private String secretKey;

    @NotBlank @Pattern(regexp = "^\\d+$") // digits only
    private String accessTokenExpiration;
}
