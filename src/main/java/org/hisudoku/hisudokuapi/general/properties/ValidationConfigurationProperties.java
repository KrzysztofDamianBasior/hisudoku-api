package org.hisudoku.hisudokuapi.general.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

// @Configuration- decorator not necessary as we decorated the main class with @ConfigurationPropertiesScan. We can also decorate main with @EnableConfigurationProperties(ConfigProperties.class) to bind the properties into the POJO.

// It is standard practice to throw an exception during application startup if required environment variables haven't been provided or if they don't meet certain validation rules
@Validated
@ConfigurationProperties(prefix = "application.validation")
@Data // configuration properties needs getters and setters
public class ValidationConfigurationProperties {
    @NotBlank
    @Min(2)
    @Max(255)
    private Integer minUsernameLength;

    @NotBlank
    @Min(2)
    @Max(255)
    private Integer maxUsernameLength;

    @NotBlank
    @Min(2)
    @Max(255)
    private Integer minPasswordLength;

    @NotBlank
    @Min(2)
    @Max(255)
    private Integer maxPasswordLength;
}
