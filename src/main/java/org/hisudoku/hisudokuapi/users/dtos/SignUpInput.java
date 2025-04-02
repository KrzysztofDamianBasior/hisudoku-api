package org.hisudoku.hisudokuapi.users.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.hisudoku.hisudokuapi.general.validators.ConfigurableSize;

@Data
@AllArgsConstructor
public class SignUpInput {
    // Message interpolation is the process used for creating error messages for Java bean validation constraints. Spring retrieves the constraint violation message details from message descriptors. Each constraint defines its default message descriptor using the message attribute. In Spring, we can use the Unified Expression Language to define our message descriptors. This allows defining error messages based on conditional logic and also enables advanced formatting options. Notice that for accessing external variables, we use ${} syntax, but for accessing other properties from the validation annotation, we use {}.
    @NotBlank(message = "{sign-up-input.username.not-blank}")
    @ConfigurableSize(
            maxProperty = "application.validation.max-username-length",
            minProperty = "application.validation.min-username-length",
            fieldName = "username"
//            message = "{sign-up-input.username.size}"
    )
    private String username;

    @Email(message = "{sign-up-input.email.email}")
    private String email;

    // String field constrained with @NotBlank must be not null, and the trimmed length must be greater than zero
    // Field (e.g. CharSequence, Collection, Map, or Array) constrained with @NotEmpty must be not null, and its size/length must be greater than zero
    @NotBlank(message = "{sign-up-input.password.not-blank}")
    //    @Size(min = 5, max = 14, message = "The author email '${validatedValue}' must be between {min} and {max} characters long")
    //    @Size(min = 3, max = 50, message = "{sign-up-input.password.size}")
    @ConfigurableSize(
            maxProperty = "application.validation.max-password-length",
            minProperty = "application.validation.min-password-length",
            fieldName = "password"
//            message = "{sign-up-input.password.size}"
    )
    private String password;
}

//@DecimalMin(value = "50", message = "The code coverage ${formatter.format('%1$.2f', validatedValue)} must be higher than {value}%")

//    @Email(groups = {ValidationStepOne.class})
//    @NotBlank(groups = {ValidationStepOne.class})
//    private String email;
//
//    @NotBlank(groups = {ValidationStepTwo.class})
//    @StrongPassword(groups = {ValidationStepTwo.class})
//    private String password;
//
//@RequestMapping(value = "stepOne")
//public String stepOne(@Validated(Account.ValidationStepOne.class) Account account) {...}
//
//@RequestMapping(value = "stepTwo")
//public String stepTwo(@Validated(Account.ValidationStepTwo.class) Account account) {...}
