package org.hisudoku.hisudokuapi.users.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.hisudoku.hisudokuapi.general.exceptions.*;
import org.hisudoku.hisudokuapi.users.dtos.SignInInput;
import org.hisudoku.hisudokuapi.users.dtos.SignUpInput;
import org.hisudoku.hisudokuapi.users.entities.EmailActivationToken;
import org.hisudoku.hisudokuapi.users.entities.HSUser;
import org.hisudoku.hisudokuapi.users.enums.Role;
import org.hisudoku.hisudokuapi.users.models.*;
import org.hisudoku.hisudokuapi.users.repositories.EmailActivationTokenComplexQueriesRepository;
import org.hisudoku.hisudokuapi.users.repositories.HSUserComplexQueriesRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final HSUserComplexQueriesRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    private final OneTimeTokenService oneTimeTokenService;
    private final EmailActivationTokenComplexQueriesRepository emailActivationTokenComplexQueriesRepository;

    public String createOttLink(String name, String relativePath) {
        // One-Time Tokens (OTT):
        //    A unique, temporary token (often a URL or a string) generated for authentication.
        //    Typically sent via email or SMS and used for passwordless login or account verification.
        //    Example: A magic link that logs you in when clicked.
        // Spring Security 6.4.0 provides robust built-in support for OTT authentication, including ready-to-use implementations
        // One-time login URL will only work once. If we need access to login again or in another device/browser then we need to generate the OTT link again.
        OneTimeToken oneTimeToken = oneTimeTokenService.generate(new GenerateOneTimeTokenRequest(name));

        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(relativePath)
                .queryParam("token", oneTimeToken.getTokenValue())
                .build()
                .toUriString();
    }

    public AuthResponseModel signUp(SignUpInput signUpInput, String lang) {
        String name = signUpInput.getUsername();
        String plainTextPassword = signUpInput.getPassword();
        String email = signUpInput.getEmail();
        if(userRepository.doesNameExist(name)){
            throw new NameTakenException(name);
        }
        if(!Objects.isNull(email)){
            if(this.userRepository.doesEmailExist(email)) {
                throw new EmailTakenException(email);
            }
        }
        HSUser newUser = userRepository.addOne(name, passwordEncoder.encode(plainTextPassword), Role.USER.name())
                .orElseThrow(() -> new OperationFailedException("sign up"));
        if(email != null) {
            // String token = jwtService.issueActivateEmailToken(email, new HSUserPrincipal(newUser), 300000); // 300000[ms] = 5[m]
            EmailActivationToken emailActivationToken = emailActivationTokenComplexQueriesRepository.addOne(email, newUser.getId())
                    .orElseThrow(() -> new OperationFailedException("sign up"));
            mailService.sendActivateEmailByUUID(email, newUser.getName(), emailActivationToken.getToken(), lang);
        }
        HSUserPrincipal newPrincipal = new HSUserPrincipal(newUser);
        String jwtToken = jwtService.issueAuthToken(newPrincipal);
        return new AuthResponseModel(jwtToken);
    }

    public AuthResponseModel signUp(String name, String email, String plainTextPassword) {
        //        if (hsUserDetailsManager.userExists(name)) {
        //            UserDetails userDetails = hsUserDetailsManager.loadUserByUsername(name);
        //            if (userDetails.isEnabled()) {
        //                throw new IllegalArgumentException("active user " + name + " already exists!");
        //            }
        //            hsUserDetailsManager.deleteUser(email);
        //        }
        if(userRepository.doesNameExist(name)){
            throw new NameTakenException(name);
        }
        HSUser newUser;
        if(email != null) { // !Objects.isNull(email)
            if(this.userRepository.doesEmailExist(email)) {
                throw new EmailTakenException(email);
            }
            newUser = userRepository.addOne(name, passwordEncoder.encode(plainTextPassword), Role.USER.name(), email)
                    .orElseThrow(() -> new OperationFailedException("sign up"));
        } else  {
            newUser = userRepository.addOne(name, passwordEncoder.encode(plainTextPassword), Role.USER.name())
                    .orElseThrow(() -> new OperationFailedException("sign up"));
        }
        HSUserPrincipal newPrincipal = new HSUserPrincipal(newUser);
        String jwtToken = jwtService.issueAuthToken(newPrincipal);

        return new AuthResponseModel(jwtToken);
    }

    public AuthResponseModel signIn(SignInInput signInInput) {
        String name = signInInput.getUsername();
        String password = signInInput.getPassword();

        // ref: https://docs.spring.io/spring-security/site/docs/3.0.x/reference/technical-overview.html
        // ref: https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html
        // 1. The username and password are obtained and combined into an instance of UsernamePasswordAuthenticationToken
        // 2. The token is passed to an instance of AuthenticationManager for validation.
        // 3. The AuthenticationManager returns a fully populated Authentication instance on successful authentication.
        // 4. The security context is established by calling SecurityContextHolder.getContext().setAuthentication(...), passing in the returned authentication object

        // The Authentication interface serves two main purposes within Spring Security:
        // 1. An input to AuthenticationManager to provide the credentials a user has provided to authenticate. When used in this scenario, isAuthenticated() returns false.
        // 2. Represent the currently authenticated user

        // The Authentication contains:
        // 1. principal: Identifies the user. When authenticating with a username/password this is often an instance of UserDetails.
        // 2. credentials: Often a password. In many cases, this is cleared after the user is authenticated, to ensure that it is not leaked.
        // 3. authorities: The GrantedAuthority instances are high-level permissions the user is granted. Two examples are roles and scopes. You can obtain GrantedAuthority instances from the Authentication.getAuthorities() method. This method provides a Collection of GrantedAuthority objects.

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(name, password));
        SecurityContextHolder.getContext().setAuthentication(authentication); // according to docs- https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html -now we set the SecurityContext on the SecurityContextHolder. Spring Security uses this information for authorization.

        // ref: https://docs.spring.io/spring-security/reference/servlet/authentication/architecture.html
        // By default, SecurityContextHolder uses a ThreadLocal to store these details, which means that the SecurityContext is always available to methods in the same thread, even if the SecurityContext is not explicitly passed around as an argument to those methods. Using a ThreadLocal in this way is quite safe if you take care to clear the thread after the present principal’s request is processed. Spring Security’s FilterChainProxy ensures that the SecurityContext is always cleared.
        // To obtain information about the authenticated principal, access the SecurityContextHolder.
        // SecurityContext context = SecurityContextHolder.getContext();
        // Authentication authentication = context.getAuthentication();
        // String username = authentication.getName();
        // Object principal = authentication.getPrincipal();
        // Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Possible exceptions
        // ref: https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/core/AuthenticationException.html
        // AuthenticationException- Abstract superclass for all exceptions related to an Authentication object being invalid for whatever reason.
        // BadCredentialsException- Thrown if an authentication request is rejected because the credentials are invalid. For this exception to be thrown, it means the account is neither locked nor disabled.
        // try {
        //     Authentication authentication = authenticationManager.authenticate
        // } catch (AuthenticationException e) {
        //   SecurityContextHolder.getContext().setAuthentication(null);
        //   LOGGER.warn("auth error:{}", e.getMessage());
        // }

        var principal = (HSUserPrincipal) authentication.getPrincipal();

        var accessToken = jwtService.issueAuthToken(principal);

        // log.debug("Successfully authenticated. Security context contains: {}", SecurityContextHolder.getContext().getAuthentication());
        return new AuthResponseModel(accessToken);
    }

//    void enableAccount() {
//        SecurityContext context = SecurityContextHolder.getContext();
//        Optional.ofNullable(context.getAuthentication())
//                .ifPresentOrElse(a -> {
//                    UserDetails principal = (UserDetails) a.getPrincipal();
//                    UserDetails disabledUser = hsUserDetailsManager.loadUserByUsername(principal.getUsername());
//                    UserDetails enabledUser = new HSUserPrincipal();
//                    userDetailsService.updateUser(enabledUser);
//                }, () -> {
//                    throw new IllegalStateException("User not logged in!");
//                });
//    }
}
