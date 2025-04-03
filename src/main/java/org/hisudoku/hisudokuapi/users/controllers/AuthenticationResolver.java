package org.hisudoku.hisudokuapi.users.controllers;

import jakarta.validation.Valid;
import java.util.Locale;

import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.users.dtos.SignInInput;
import org.hisudoku.hisudokuapi.users.dtos.SignUpInput;
import org.hisudoku.hisudokuapi.users.models.AuthResponseModel;
import org.hisudoku.hisudokuapi.users.services.AuthenticationService;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import graphql.schema.DataFetchingEnvironment;
import graphql.GraphQLContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequiredArgsConstructor
public class AuthenticationResolver {
    private final AuthenticationService authenticationService;
    private final Logger logger = LoggerFactory.getLogger(AuthenticationResolver.class);

    // no auth required
    @PreAuthorize("permitAll()")
    @SchemaMapping(typeName = "Mutation", field = "signUp")
    public AuthResponseModel signUp(
            @Argument @Valid SignUpInput signUpInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext,
            Locale locale // The default implementation is, AcceptHeaderLocaleResolver simply using the request's locale provided by the respective HTTP header.
            // @RequestHeader(name = "Accept-Language", required = false) final Locale locale,
            // HttpServletRequest request
    ){
        //      String headerLang = request.getHeader("Accept-Language");
        //      return headerLang == null || headerLang.isEmpty() ? Locale.getDefault() : Locale.lookup(Locale.LanguageRange.parse(headerLang), LOCALES);
        logger.info("Mutation signUp. The client locale is {}.", locale);

        // How to obtain a current user locale from Spring without passing it as a parameter to functions?
        // ref: https://docs.spring.io/spring-framework/docs/1.2.x/javadoc-api/org/springframework/context/i18n/LocaleContextHolder.html
        //  LocaleContextHolder - Simple holder class that associates a LocaleContext instance with the current thread. The LocaleContext will be inherited by any child threads spawned by the current thread. Used as a central holder for the current Locale in Spring, wherever necessary: for example, in MessageSourceAccessor. DispatcherServlet automatically exposes its current Locale here. Other applications can expose theirs too, to make classes like MessageSourceAccessor automatically use that Locale.
        // getLocale() -Return the Locale associated with the current thread, if any, or the system default Locale else.
        // getLocaleContext() -Return the LocaleContext associated with the current thread, if any.
        // setLocale(Locale locale) -Associate the given Locale with the current thread.
        // resetLocaleContext() -Reset the LocaleContext for the current thread.
        // So it turns out, though, that Spring MVC does store the locale in thread local storage. It can be accessed by:
        //Locale locale = LocaleContextHolder.getLocale();

        // In Java web applications, locale information can be retrieved from ServletRequest ( and HttpServletRequest ) object obtained on the server-side
        // Locale currentLocale = httpServletRequest.getLocale();
        //
        // System.out.println(currentLocale.getDisplayLanguage());  //English
        // System.out.println(currentLocale.getDisplayCountry());	//United States
        // System.out.println(currentLocale.getLanguage());		    //en
        // System.out.println(currentLocale.getCountry());			//US
        //
        // In Java desktop applications, locale information is retrieved using Locale.getDefault() that returns the default locale set in the Java Virtual Machine. We can also use system properties "user.country" and "user.language" for this information. The Java Virtual Machine sets the default locale during startup based on the host machine environment and preferences.
        // Locale currentLocale = Locale.getDefault();
        // System.out.println(System.getProperty("user.language"));  //en
        // System.out.println(System.getProperty("user.country"));  //US

        // Locale -> String getLanguage() -This method returns the language code for this locale, which will either be the empty string or a lowercase ISO 639 code.
        return authenticationService.signUp(signUpInput, locale.getLanguage());
    }

    // no auth required
    @PreAuthorize("permitAll()")
    @SchemaMapping(typeName = "Mutation", field = "signIn")
    public AuthResponseModel signIn(
            @Argument @Valid SignInInput signInInput,
            DataFetchingEnvironment env,
            GraphQLContext graphQLContext
    ) {
        return authenticationService.signIn(signInInput);
    }
}
