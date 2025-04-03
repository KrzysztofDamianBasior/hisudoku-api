package org.hisudoku.hisudokuapi.users.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.users.dtos.ForgotPasswordInput;
import org.hisudoku.hisudokuapi.users.models.HSUserPrincipal;
import org.hisudoku.hisudokuapi.users.services.AuthenticationService;
import org.hisudoku.hisudokuapi.users.services.UserActionsService;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

//@RequestMapping("/")
@Validated
@Controller
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserActionsService userActionsService;

    // -dtos------------------------------------------------------------------------------------------------------------

    record ChangePasswordForm(@NotBlank String password, @NotBlank String confirmPassword) {
    }

    record OttReason(String title, String action, String reason) {
    }

    record CreateAccountForm(@NotBlank String name, @Email String email, @NotBlank String password) {
    }

    // -serving views---------------------------------------------------------------------------------------------------

    @GetMapping("/")
    String showIndex(
            @RequestParam(name = "logout", defaultValue = "true", required = false) boolean logout
    ) {
        return "views/index";
    }

    @GetMapping("/user")
    String showChangePasswordForm(
            @RequestParam(name = "chpwd", required = false, defaultValue = "true") boolean changePassword,
            @ModelAttribute ChangePasswordForm changePasswordForm
    ) {
        return "views/account/profile";
    }

    @GetMapping(value = {"/account", "/account/sign-in", "/account/login"})
    String showSignInForm(
            @RequestParam(required = false, defaultValue = "true") boolean error,
            @RequestParam(required = false, defaultValue = "true", name = "continue") boolean continueFlag
    ) {
        return "views/account/sign-in";
    }

    // Thanks to @ModelAttribute Spring MVC supply this object to a Controller method, the annotation can be used also above getter to define objects which should be part of a Model. For parameter annotations, we can think of @ModelAttribute as the equivalent of @Autowired + @Qualifier i.e. it tries to retrieve a bean with the given name from the Spring managed model. If the named bean is not found, instead of throwing an error or returning null, it implicitly takes on the role of @Bean i.e. Create a new instance using the default constructor and add the bean to the model. For method annotations, we can think of @ModelAttribute as the equivalent of @Bean + @Before, i.e. it puts the bean constructed by user's code in the model and it's always called before a request handling method.
    @GetMapping("/account/register")
    String showRegistrationForm(@ModelAttribute CreateAccountForm createAccountForm) {
        return "views/account/sign-up";
    }

    @GetMapping("/account/logout")
    String showLogoutButton() {
        return "views/account/logout";
    }

    @GetMapping("/account/forgot-password")
    String showSendEmailToResetPasswordForm() {
        return "views/account/forgot-password";
    }

    @GetMapping("/account/ott-sent")
    String showOttLinkSentInfo() {
        // ref: https://www.baeldung.com/spring-web-flash-attributes
        // A naive way to engineer a web form would be to use a single HTTP POST request that takes care of submission and gives back a confirmation through its response. However, such design exposes the risk of duplicate processing of POST requests, in case the user ends up refreshing the page.
        //To mitigate the issue of duplicate processing, we can create the workflow as a sequence of interconnected requests in a specific order — namely, POST, REDIRECT, and GET. In short, we call this the Post/Redirect/Get (PRG) pattern for form submission.
        //On receiving the POST request, the server processes it and then transfers control to make a GET request. Subsequently, the confirmation page displays based on the response of the GET request. Ideally, even if the last GET request gets attempted more than once, there shouldn’t be any adverse side-effects
        // To complete the form submission using the PRG pattern, we’ll need to transfer information from the initial POST request to the final GET request after redirection.
        //Unfortunately, we can neither use the RequestAttributes nor the SessionAttributes. That’s because the former won’t survive a redirection across different controllers, while the latter will last for the entire session even after the form submission is over.
        //But, we don’t need to worry as Spring’s web framework provides flash attributes that can fix this exact problem.
        //Every request has two FlashMap instances, namely Input FlashMap and Output FlashMap, which play an important role in the PRG pattern:
        //    Output FlashMap is used in the POST request to temporarily save the flash attributes and send them to the next GET request after the redirect
        //    Input FlashMap is used in the final GET request to access the read-only flash attributes that were sent by the previous POST request before the redirect
        return "views/account/ott-sent";
    }

    @GetMapping("/account/reset-password")
    String showOttSubmitForm(
            @RequestParam(value = "token") String token,
            Model model
            // ModelMap modelMap
    ) {
        //        modelMap.addAttribute("message","CodingNomads!");
        //        modelMap.size()
        //        modelMap.containsKey()

        // Spring MVC does store the locale in thread local storage. It can be accessed by:
        // Locale locale = LocaleContextHolder.getLocale();

        // HttpServletRequest request, HttpServletResponse response
        // LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);

        // if (localeResolver instanceof AcceptHeaderLocaleResolver headerLocaleResolver) {
        //     localeHolder.setCurrentLocale(headerLocaleResolver.resolveLocale(request));
        // }

        //     AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        //     localeResolver.setDefaultLocale(Locale.ENGLISH);
        //     "/{language}/","/{language}/home"

        // List<Locale> LOCALES = Arrays.asList(new Locale("en"), new Locale("fr"));
        // String headerLang = request.getHeader("Accept-Language");
        // return headerLang == null || headerLang.isEmpty() ? Locale.getDefault() : Locale.lookup(Locale.LanguageRange.parse(headerLang), LOCALES)
        model.addAttribute("currentLocale", LocaleContextHolder.getLocale().getDisplayName());
        model.addAttribute("ottReason", new OttReason("Password reset", "Reset password", "change-password"));
        //        Map<String, String> map = new HashMap<>();
        //        map.put("name", "Developer");
        //        map.put("language", "Java");
        //        map.put("framework", "Spring");
        //        model.mergeAttributes(map);

        //    String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        //    String confirmationUrl = contextPath + "/regitrationConfirm.html?token=" + newToken.getToken();
        //    redirect:/badUser.html?lang=" + locale.getLanguage();  // Locale locale is a controller method arg

        //return "redirect:/login/ott?token="+token;

        return "views/account/ott-login";
    }

    // -processing input------------------------------------------------------------------------------------------------

    @PostMapping("/user/change-password")
    String changePassword(
            @Valid ChangePasswordForm changePasswordForm,
            BindingResult result,
            Model model,
            RedirectAttributes attributes,
            @AuthenticationPrincipal HSUserPrincipal principal
    ) {
        // We annotate DTO with @Valid this indicates that Java Bean Validation validates the object. We can examine the validation outcomes via the BindingResult object. Whenever there is any field error, which is determined by bindingResult.hasFieldErrors(), Spring Boot fetches the localized error message for us according to the current locale and encapsulates the message into a field error instance. When validating a model attribute in Spring MVC, it’s crucial to place the BindingResult parameter right after the @Valid annotated parameter. Doing this ensures that any validation errors are correctly captured and handled by Spring.
        //      if (bindingResult.hasFieldErrors()) {
        //            List<InputFieldError> fieldErrorList = bindingResult.getFieldErrors().stream()
        //              .map(error -> new InputFieldError(error.getField(), error.getDefaultMessage()))
        //              .collect(Collectors.toList());
        //            UpdateUserResponse updateResponse = new UpdateUserResponse(fieldErrorList);
        //            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(updateResponse);
        //        } else {
        //            return ResponseEntity.status(HttpStatus.OK).build();
        //        }
        //
        //        if (bindingResult.hasErrors()) {
        //            return "errors/addUser";
        //        }

        userActionsService.updatePassword(principal, changePasswordForm.password);
        attributes.addFlashAttribute("message", "Password successfully changed!");
        return "redirect:/user";
    }

    @PostMapping("/account/password-reset")
    String requestPasswordReset(String email, RedirectAttributes attributes) {
        userActionsService.requestUpdatePasswordByOTT(new ForgotPasswordInput(email), LocaleContextHolder.getLocale().getLanguage());

        // ref: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/flash-attributes.html
        //Flash attributes provide a way for one request to store attributes that are intended for use in another. This is most commonly needed when redirecting for example, the Post-Redirect-Get pattern. Flash attributes are saved temporarily before the redirect (typically in the session) to be made available to the request after the redirect and are removed immediately.
        //Spring MVC has two main abstractions in support of flash attributes. FlashMap is used to hold flash attributes, while FlashMapManager is used to store, retrieve, and manage FlashMap instances.
        //Flash attribute support is always “on” and does not need to be enabled explicitly. However, if not used, it never causes HTTP session creation. On each request, there is an “input” FlashMap with attributes passed from a previous request (if any) and an “output” FlashMap with attributes to save for a subsequent request. Both FlashMap instances are accessible from anywhere in Spring MVC through static methods in RequestContextUtils.
        //Annotated controllers typically do not need to work with FlashMap directly. Instead, a @RequestMapping method can accept an argument of type RedirectAttributes and use it to add flash attributes for a redirect scenario. Flash attributes added through RedirectAttributes are automatically propagated to the “output” FlashMap. Similarly, after the redirect, attributes from the “input” FlashMap are automatically added to the Model of the controller that serves the target URL.
        attributes.addFlashAttribute("repeatActionPart", "forgot-password");
        attributes.addFlashAttribute("message", "We have sent you mail to reset your password!");

        // String[] attrArray = {"london", "paris"};
        // Map<String, String[]> attrMap = new HashMap<>();
        // attrMap.put("place", attrArray);
        // redirectAttrs.mergeAttributes(attrMap);
        // This will create a URL with request parameters like this: place=london%2Cparis (where %2C is the ASCII keycode in hexadecimal for a comma), which is equivalent to place=london&place=paris

        // new ModelAndView().setViewName("redirect:/account/ott-sent");
        return "redirect:/account/ott-sent";
    }

    @PostMapping("/account/register")
    String registerAccount(
            @Valid CreateAccountForm createAccountForm,
            BindingResult result,
            RedirectAttributes attributes,
            // Principal principal,
            // Authentication authentication,
            // @AuthenticationPrincipal UserDetails userDetails,
            // @AuthenticationPrincipal String username,
            @AuthenticationPrincipal HSUserPrincipal principal
    ) {
        // UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //if (!(authentication instanceof AnonymousAuthenticationToken)) {
        //    String currentUserName = authentication.getName();
        //    return currentUserName;
        //}

        // if(StringUtils.isEmpty(createAccountForm)){
        //    model.addAttribute("errorMessage","Login failed"); // <span th:if="${errorMessage}" th:text="${errorMessage}">Error</span>
        //    return "index";
        //}

        //    String err = validationService.validateUser(createAccountForm);
        //    if (!err.isEmpty()) {
        //        ObjectError error = new ObjectError("globalError", err);
        //        result.addError(error);
        //    }
        //    if (result.hasErrors()) {
        //        return "errors/addUser";
        //    }

        //<div th:if="${#fields.hasErrors('global')}">
        //    <h3>Global errors:</h3>
        //    <p th:each="err : ${#fields.errors('global')}" th:text="${err}" class="error" />
        //</div>

        try {
            authenticationService.signUp(createAccountForm.name, createAccountForm.email, createAccountForm.password);
        } catch (IllegalArgumentException e) {
            attributes.addFlashAttribute("createAccountForm", createAccountForm);
            attributes.addFlashAttribute("error", e.getLocalizedMessage());
            return "redirect:/account/register";
        }
        return "redirect:/account/sign-in";
    }

    //    @ExceptionHandler(IllegalArgumentException.class)
    //    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
    //        model.addAttribute("errorMessage", ex.getMessage());
    //        return "error";
    //    }

    //You can use the @ResponseStatus annotation to map exceptions to specific HTTP status codes.
    //import org.springframework.http.HttpStatus;
    //import org.springframework.web.bind.annotation.ResponseStatus;
    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    //public class BadRequestException extends RuntimeException {
    //    public BadRequestException(String message) {
    //        super(message);
    //    }
    //}

    //When a method parameter is annotated with @ModelAttribute, Spring goes through the following steps:
    //    Lookup: Spring first tries to find an existing model attribute with the same name as the parameter’s name (“book” in the above example).
    //    Instantiation: If no existing model attribute is found, Spring will instantiate a new object of the corresponding class.
    //    Population: Spring then takes each form field and matches it against the properties in the model object, populating them using their corresponding setter methods.
    //    Addition to Model: Finally, the populated object is added to the model, making it available for rendering in the view layer.
    //@ModelAttribute("cities")
    // public List<String> checkOptions(){
    // return new Arrays.asList(new[]{"Sofia","Pleven","Ruse"});//and so on
    //}
}
