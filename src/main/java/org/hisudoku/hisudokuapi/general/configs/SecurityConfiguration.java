package org.hisudoku.hisudokuapi.general.configs;

import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.general.filters.JWTAuthenticationFilter;
import org.hisudoku.hisudokuapi.general.handlers.UnauthorizedHandler;
import org.hisudoku.hisudokuapi.users.enums.Role;
import org.hisudoku.hisudokuapi.users.services.HSUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.ott.InMemoryOneTimeTokenService;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

// @EnableWebSecurity is a marker annotation. It allows Spring to find and automatically apply the class to the global WebSecurity. It's to switch off the default web application security configuration and add your own. EnableWebSecurity will provide configuration via HttpSecurity. It allows to configure access based on urls patterns, the authentication endpoints, handlers etc.
//
// @EnableGlobalMethodSecurity can be added to any class with the @Configuration annotation. @EnableGlobalMethodSecurity is a global configuration annotation that enables method security across the entire application using an annotation-driven approach. You have to explicitly enable the method-level security annotations, otherwise, they’re ignored. EnableGlobalMethodSecurity provides AOP security on methods. Some of the annotations that it provides are PreAuthorize, PostAuthorize. You can also enable @Secured, an older Spring Security annotation, and JSR-250 annotations. Annotation @EnableGlobalMethodSecurity has become deprecated and was replaced with @EnableMethodSecurity. The rationale behind this change is that with @EnableMethodSecurity property prePostEnabled needed to enable use of @PreAuthorize/@PostAuthorize and @PreFilter/@PostFilter is by default set to true. So you no longer need to write prePostEnabled = true, just annotating your configuration class with @EnableMethodSecurity would be enough.
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(
        // prePostEnabled = true, // The prePostEnabled property enables Spring Security pre/post annotations. Supports Spring Expression Language like @PreAuthorize("hasRole('ROLE_ADMIN') or hasAuthority('Admin') and #username == authentication.principal.username)")
        // securedEnabled = true, // The securedEnabled property determines if the @Secured annotation should be enabled. Thanks to this flag it becomes possible to use annotations like this @Secured({ "ROLE_USER", "ROLE_ADMIN" })
        // jsr250Enabled = true // The jsr250Enabled property allows us to use the @RoleAllowed annotation. The @RolesAllowed annotation is the JSR-250’s equivalent annotation of the @Secured annotation- @RolesAllowed({ "ROLE_USER", "ROLE_ADMIN" })
)
public class SecurityConfiguration {
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final UnauthorizedHandler unauthorizedHandler; // JwtAuthEntryPoint
    private final HSUserDetailsService hsUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    OneTimeTokenService oneTimeTokenService() {
        return new InMemoryOneTimeTokenService();
    }

//    @Bean
//    InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {
//        InMemoryUserDetailsManager users = new InMemoryUserDetailsManager();
//        users.createUser(User.builder()
//                .username("bob@example.com")
//                .password(encoder.encode("bob"))
//                .build());
//        return users;
//    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        return new HSUserDetailsManager(repository);
//    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(hsUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

//    // The object mapper becomes a bean once you add the spring web starter, let's customize it
//    @Bean
//    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
//        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
//        builder.modules(new JavaTimeModule());
//
//        // for example: Use created_at instead of createdAt
//        builder.propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
//
//        // skip null fields'
//        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
//        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        return builder;
//    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        Access-Control-Allow-Origin: Defines which origins may have access to the resource. A ‘*’ represents any origin
//        Access-Control-Allow-Methods: Indicates the allowed HTTP methods for cross-origin requests
//        Access-Control-Allow-Headers: Indicates the allowed request headers for cross-origin requests
//        Access-Control-Max-Age: Defines the expiration time of the result of the cached preflight request
//        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
//        configuration.setExposedHeaders(Arrays.asList("X-Get-Header"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain configureApplicationSecurity(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) // Spring Security enables Cross-Site Request Forgery (CSRF) protection by default. CSRF is an attack that tricks the victim into submitting a malicious request, and uses the identity of the victim to perform an undesired function on their behalf. If the CSRF token, which is used to protect against this type of attack, is missing or incorrect, the server may also respond with error 403.
                // .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // in STATELESS mode Spring Security will never create a HttpSession, and it will never use it to get the SecurityContext.
                .authenticationProvider(authenticationProvider())
                .exceptionHandling(h -> h.authenticationEntryPoint(unauthorizedHandler)) // Spring security exceptions are commenced at the AuthenticationEntryPoint
                .securityMatcher("/**") // it makes the security configuration to be applied to all controllers, ** is a pattern to match any number of directories and subdirectories in a URL. The * pattern is a pattern that matches any URL and has exactly one level of a subdirectory
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers("/auth/**", "/user/**", "/account/**", "/error", "/views/**" , "/", "/*.*").permitAll() // Relax security for account management
                        .requestMatchers("/graphiql", "/graphql", "/graphql/**").permitAll() // Relax security for graphql endpoint
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/actuator").hasRole(Role.ADMIN.name()) // pattern /actuator would match all existing aliases of that path like "/actuator", "/actuator/", "/actuator.html"
                        .requestMatchers("/actuator/**").hasRole(Role.ADMIN.name())
//                        .anyRequest().permitAll()
                        .anyRequest().authenticated()
                )
                //.formLogin(AbstractHttpConfigurer::disable)
                .formLogin(flc -> flc
                        .loginPage("/account/login")
                        .usernameParameter("name")
                        .defaultSuccessUrl("/user")
                )
                .logout(lc -> lc
                        .logoutUrl("/account/logout")
                        .logoutSuccessUrl("/?logout"))
                // ref: https://docs.spring.io/spring-security/reference/servlet/authentication/onetimetoken.html
                // OneTimeTokenLoginConfigurer class is used to customize the default behavior of OTT login, including:
                //    Configures the filters DefaultLoginPageGeneratingFilter for token generation URL and DefaultOneTimeTokenSubmitPageGeneratingFilter for token processing URL. When submitting token, only POST requests are processed, so make sure to pass a valid CSRF token if CSRF protection is enabled.
                //    Configures HTML templates for token generation and submit URLs.
                //    Configures InMemoryOneTimeTokenService which is an in-memory implementation of the OneTimeTokenService interface that uses a ConcurrentHashMap to store the generated OneTimeToken. A random UUID is used as the token value.
                //    Configures SimpleUrlAuthenticationFailureHandler for redirecting to “/login?error” when authentication fails.
                //    Configures SavedRequestAwareAuthenticationSuccessHandler which redirects the request to originally requested path after successful authentication.
                //
                // user uses /ott/generate endpoint to request new token generation, this is intercepted by GenerateOneTimeTokenFilter, which uses the OneTimeTokenService to generate the new token for the username, token is passed to OneTimeTokenGenerationSuccessHandler to generate ott link and use the mail service to send the link to the user, lastly we display the web page to notify the user the token was sent
                //
                //once the user clicks the link in the email, we come to token consumption part, user submits the token to the /login/ott endpoint, which is secured endpoint and goes to the Spring Security filter chain, it comes to the OneTimeTokenAuthenticationConverter, which creates OneTimeTokenAuthenticationToken that is passed to the authentication providers, and there is a OneTimeTokenAuthenticationProvider that can check and consume the token, it consumes the token and check th token by OneTimeTokenService, if the token is valid, user is authenticated and logged in
                .oneTimeTokenLogin(ottc -> ottc
                                // By default, the GenerateOneTimeTokenFilter listens to POST /ott/generate requests. That URL can be changed by using the generateTokenUrl(String) DSL method:
                                // .generateTokenUrl("/ott/my-generate-url")
                                // The default One-Time Token submit page is generated by the DefaultOneTimeTokenSubmitPageGeneratingFilter and listens to GET /login/ott. The URL can also be changed, like so:
                                //.submitPageUrl("/ott/submit")
                                // If you want to use your own One-Time Token submit page, you can disable the default page and then provide your own endpoint.
                                .showDefaultSubmitPage(false)
                                .loginProcessingUrl("/account/ott-submit")
                                .authenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/account/login?error"))
//                        ottc.loginProcessingUrl("/custom/submit-token"); // custom login processing url
//                        ottc.defaultSubmitPageUrl("/custom/submit"); // custom success url
//                        ottc.tokenGeneratingUrl("/custom/generate-token"); // custom token generating url
//                        ottc.tokenService(dbOneTimeTokenService);
                );
        // .oneTimeTokenLogin(Customizer.withDefaults());  // Uses InMemoryOneTimeTokenService by default

        // ref: https://docs.spring.io/spring-security/reference/servlet/authentication/onetimetoken.html
        // ref: https://howtodoinjava.com/spring-security/one-time-token-login-authentication/
        // ref: https://dev.to/ravitejadaggupati/implementing-one-time-token-authentication-with-spring-security-1fpg
        // The interface that define the common operations for generating and consuming one-time tokens is the OneTimeTokenService. Spring Security uses the InMemoryOneTimeTokenService as the default implementation of that interface, if none is provided. For production environments consider using JdbcOneTimeTokenService.Some of the most common reasons to customize the OneTimeTokenService are, but not limited to: Changing the one-time token expire time, Storing more information from the generate token request, Changing how the token value is created, Additional validation when consuming a one-time token
        // The above configuration uses the default InMemoryOneTimeTokenService which uses ConcurrentHashMap for storing the tokens and corresponding usernames. So as soon as we restart the server, all the generated tokens will be lost and thus become invalid. Also, the default generated tokens are valid only for 5 minutes which may not be sufficient duration to allow the user to login. Different approach is to store the token and their expiration time in the database, what prevent the loss of tokens during server restarts and also gives us a chance to configure a custom token expiration time. To do this we can create appropriate table
        // CREATE TABLE one_time_tokens (
        //     token_value VARCHAR(255) PRIMARY KEY,
        //     username VARCHAR(255) NOT NULL,
        //     issued_at TIMESTAMP NOT NULL,
        //     expires_at TIMESTAMP NOT NULL,
        //     used BOOLEAN NOT NULL
        // );
        //
        //@Entity
        //@Table(name = "one_time_tokens")
        //public class OneTimeTokenEntity {
        //    @Id
        //    @Column(name = "token_value")
        //    private String tokenValue;
        //
        //    @Column(name = "username")
        //    private String username;
        //
        //    @Column(name = "issued_at")
        //    private LocalDateTime createdAt;
        //
        //    @Column(name = "expires_at")
        //    private LocalDateTime expiresAt;
        //
        //    @Column(name = "used")
        //    private boolean used;
        //}
        //
        // Optional<OneTimeTokenEntity> findByTokenValueAndUsedFalse(String tokenValue);
        //
        // and than inject JdbcOneTimeTokenService
        // JdbcTemplate jdbcTemplate;
        //
        // @Bean
        // public OneTimeTokenService oneTimeTokenService() {
        //     return new JdbcOneTimeTokenService(jdbcTemplate);
        // }
        //
        //  schedule a task that runs every 1 hour and remove all expired tokens from the database
        //  @Scheduled(fixedRate = 3600000) // 3600000 ms = 1 hour
        //  @Transactional
        //  public void cleanUpOldRecords() {
        //
        //    System.out.println("Deleting expired tokens");
        //    int deleted = tokenRepository.deleteExpiredTokens(Instant.now());
        //    System.out.println("Deleted " + deleted + " expired tokens");
        //  }
        //}
        //@Repository
        //public interface OttTokenRepository extends JpaRepository<OttToken, Long> {
        //  @Modifying
        //  @Transactional
        //  @Query("DELETE FROM ott e WHERE e.expiresAt < :currentTimestamp")
        //  int deleteExpiredTokens(Instant currentTimestamp);
        //}

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

//    @Override
//    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addViewController("/home").setViewName("home");
//        registry.addViewController("/").setViewName("home");
//    }
}
