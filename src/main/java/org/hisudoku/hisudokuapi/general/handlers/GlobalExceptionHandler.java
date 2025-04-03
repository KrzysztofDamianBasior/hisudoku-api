package org.hisudoku.hisudokuapi.general.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import org.hisudoku.hisudokuapi.users.controllers.AuthenticationController;
import org.hisudoku.hisudokuapi.general.exceptions.*;

import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

// ref: https://codersee.com/exception-handling-with-restcontrolleradvice-and-exceptionhandler/
// @ControllerAdvice, has been introduced in Spring 3.2 and allows us to write a code, which will be applied globally to all controllers. It can be used to add model enhancements methods, binder initialization methods, and to handle exceptions. @RestControllerAdvice is a specialized annotation in Spring that combines @ControllerAdvice (global exception handling) and @ResponseBody (auto-serialization to JSON/XML). It acts as a global interceptor for exceptions thrown by @RestController classes,  allowing you to handle exceptions globally across all your REST controllers and return structured JSON/XML responses. It ensures that error responses are returned in required JSON/XML format instead of HTML. @ResponseBody indicates that our methods’ return values should be bound to the web response body. To put it simply- our handler methods returning some ExampleClass will be treated as ResponseEntity<ExampleClass> out of the box. Since @RestControllerAdvice combines @ControllerAdvice with @ResponseBody, it serializes exception responses based on the request’s Accept header. If the Accept header is missing, Spring Boot defaults to JSON unless XML is explicitly configured as a priority. The @RestControllerAdvice annotation is specialization of @Component annotation so that it is auto-detected via classpath scanning. RestControllerAdvice is registered as a spring bean and ExceptionHandler can be applied globally. When we use @ControllerAdvice instead of @RestControllerAdvice we need to return ResponseEntity type and determine the response type in return like: return ResponseEntity.badRequest().body(errors);

// ref: https://medium.com/@seonggil/handling-exceptions-with-restcontrolleradvice-exceptionhandler-e7c95216da8d
// By default, when an exception occurs, error handling depends on the environment you accessed. If you have accessed the web page, you will receive the Whitelab (the term "whitelabel" refers to the fact that the page is unbranded and lacks any custom styling or information, making it a default response for errors) error page. The spring boot has a BasicErrorController implemented for error handling, and Spring Boot by default has a WAS setup that forwards error requests back to /error on exceptions.
// request flow: WAS -> Filter -> DispatcherServlet -> Interceptor -> Controller
//If an exception occurs in the Controller(Service, DB…), it will call the /error once more.
//WAS -> Filter -> DispatcherServlet -> Interceptor -> Controller -> (throw Exception) -> Interceptor -> DispatcherServlet -> Filter -> WAS -> Filter -> DispatcherServlet -> Interceptor Controller(/error)
// Exception handling flow
//     ExceptionHandlerExceptionResolver works
//    - Check if there is an appropriate ExceptionHandler in the controller that threw the exception
//    - If there is no suitable ExceptionHandler, go to ControllerAdvice
//    - Checks if there is a suitable ExceptionHandler in ControllerAdvice, and if not, it goes to the next handler
//    ResponseStatusExceptionResolver works
//    - Check if ResponseStatus exists or ResponseStatusException
//    - If so, the exception is passed to the servlet by sendError() in the ServletResponse and the servlet passes the request to the BasicErrorController.
//    DefaultHandlerExceptionResolver works
//    - Check if it is an internal exception of Spring, and if it is correct, handle the error, otherwise skip
//    Because there is no suitable ExceptionResolver, the exception is passed to the servlet, which passes the request back to the BasicErrorController according to the automatic configuration done by SpringBoot.

// ref: https://bootcamptoprod.com/spring-boot-restcontrolleradvice-annotation/
// By default, @RestControllerAdvice applies globally to all @RestController classes in a Spring Boot application. However, we can restrict its scope to specific controllers using the basePackages, basePackageClasses, or assignableTypes attributes. This allows for more granular exception handling, ensuring that only selected controllers are affected
// @RestControllerAdvice(basePackages = "com.example.user") - This @RestControllerAdvice will only apply to controllers inside the “com.example.user” package. Other controllers in different packages will not be affected by this advice.
// @RestControllerAdvice(basePackageClasses = {UserController.class}) - instead of specifying package names as strings, you can reference one or more classes. The package of each specified class will be scanned, and exception handling will be applied to all controllers within those packages. The exception handler applies to all controllers within the package(s) of the specified class(es). Unlike basePackages, this is not string-based but instead relies on actual class references. It is a type-safe approach, avoiding issues with incorrect package name strings. Multiple classes can be specified, and their respective packages will all be scanned.
// @RestControllerAdvice(assignableTypes = {UserController.class, AdminController.class}) - Instead of specifying entire packages, you can target specific controllers using assignableTypes. This is useful when you want to apply custom exception handling to selected controllers. This advice only applies to UserController and AdminController. Other controllers in the project will not use this exception handler.

@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER")
@ControllerAdvice(assignableTypes = {AuthenticationController.class})
// interceptor that allows us to use the same exception handling across multiple endpoints
public class GlobalExceptionHandler {
    // Internationalization is the process of creating an application that can be adapted to different languages and regions.
    // The class MessageSource from org.springframework.context provides methods to receive translated messages. SpringBoot will create this bean for us. Then, it is injected in the classes where messages will be used.
    //     private final ResourceBundleMessageSource source;
    //     source.getMessage(greetings, new Object[]{username}, locale);

    //  MessageSource is strategy interface for resolving messages, with support for the parameterization and internationalization of such messages. Spring provides two out-of-the-box implementations for production:
    //    ResourceBundleMessageSource, built on top of the standard ResourceBundle
    //    ReloadableResourceBundleMessageSource, being able to reload message definitions without restarting the VM

    @Autowired
    private MessageSource messages;

    //    @Autowired
    //    private LocaleResolver localeResolver;
    //    Locale locale = localeResolver.resolveLocale(request);

//    @ExceptionHandler({MailAuthenticationException.class})
//    public String handleMailAuthenticationException(RuntimeException ex, Model model, WebRequest request, Locale locale) {
//        // messages.getMessage("message.userNotFound", new Object[]{ ex.getTheClassName(), ex.getId()}, request.getLocale())
//        model.addAttribute("errorMessage", messages.getMessage("exceptions.email-config-error", null, request.getLocale()));
//        return "error";
//    }

    @ExceptionHandler(SudokuNotFoundException.class)
    public ModelAndView handleSudokuNotFoundException(HttpServletRequest request, SudokuNotFoundException ex, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", messages.getMessage("exceptions.sudoku-not-found-exception", new Object[]{ex.getSudokuId()}, request.getLocale()));
        modelAndView.setViewName("views/error");
        return modelAndView;
    }

    //    @ExceptionHandler(SudokuNotFoundException.class)
    //    public ResponseEntity<ExceptionObject> handleSudokuNotFoundException(SudokuNotFoundException ex, WebRequest request) {
    //        ExceptionObject exceptionObject = new ExceptionObject();
    //        exceptionObject.setStatusCode(HttpStatus.NOT_FOUND.value());
    //        exceptionObject.setMessage(ex.getMessage());
    //        exceptionObject.setTimestamp(new Date());
    //        return new ResponseEntity<ExceptionObject>(exceptionObject, HttpStatus.NOT_FOUND);
    //    }
    //@Data
    //class ExceptionObject {
    //    private Integer statusCode;
    //    private String message;
    //    private Date timestamp;
    //}

    @ExceptionHandler(EmailActivationTokenNotFound.class)
    public ModelAndView handleEmailActivationTokenNotFound(HttpServletRequest request, EmailActivationTokenNotFound ex, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", messages.getMessage("exceptions.email-activation-token-not-found-exception", new Object[]{ex.getToken()}, request.getLocale()));
        modelAndView.setViewName("views/error");
        return modelAndView;
    }

    @ExceptionHandler(EmailTakenException.class)
    public ModelAndView handleEmailTakenException(EmailTakenException ex, Model model, WebRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", messages.getMessage("exceptions.email-taken-exception", new Object[]{ex.getEmail()}, request.getLocale()));
        modelAndView.setViewName("views/error");
        return modelAndView;
    }

    @ExceptionHandler(OperationFailedException.class)
    public ModelAndView handleOperationFailedException(OperationFailedException ex, Model model, WebRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", messages.getMessage("exceptions.operation-failed-exception", new Object[]{ex.getReason()}, request.getLocale()));
        modelAndView.setViewName("views/error");
        return modelAndView;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFoundException(UserNotFoundException ex, Model model, WebRequest request) {
        // import org.springframework.security.core.userdetails.UsernameNotFoundException;
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", messages.getMessage("exceptions.user-not-found-exception", null, request.getLocale()));
        modelAndView.setViewName("views/error");
        return modelAndView;
    }

    @ExceptionHandler(NameTakenException.class)
    public ModelAndView handleNameTakenException(NameTakenException ex, Model model, WebRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", messages.getMessage("exceptions.name-taken-exception", new Object[]{ex.getUsername()}, request.getLocale()));
        modelAndView.setViewName("views/error");
        return modelAndView;
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    public ModelAndView handleOperationNotAllowedException(OperationNotAllowedException ex, Model model, WebRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", messages.getMessage("exceptions.operation-not-allowed-exception", new Object[]{ex.getOperation()}, request.getLocale()));
        modelAndView.setViewName("views/error");
        return modelAndView;
    }

    // ConstraintViolationException is thrown when an action violates a constraint placed on a repository structure.
    @ExceptionHandler(ConstraintViolationException.class)
    ModelAndView handleConstraintViolationException(ConstraintViolationException e, Model model, WebRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", messages.getMessage("exceptions.constraint-violation-exception", null, request.getLocale()));
        modelAndView.setViewName("views/error");
        return modelAndView;
    }

    // MethodArgumentNotValidException is thrown when validation on an argument annotated with @Valid fails.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ModelAndView handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, Model model, WebRequest request) {
        // BindingResult bindingResult = ex.getBindingResult();
        //        {
        //            "username": "must not be blank" ,
        //            "id": "must be more or equal to 0",
        //            "password": "invalid password entered"
        //        }

        Map<String, String> errors = new HashMap<>();

        // ref: https://stackoverflow.com/questions/37781632/fielderror-vs-objecterror-vs-global-error
        // getAllErrors() returns all errors, both Global and Field. getFieldErrors() only returns errors related to binding field values
        // "global error" is any ObjectError that is not an instance of a FieldError, getGlobalError() returns an ObjectError
        // If you only log FieldErrors, you will miss any ObjectErrors that code registered as a "global error" e.g. by calling BindingResult.reject(errorCode, errorArgs, defaultMessage). Typically, errors are registered against fields of the validated/bound object as opposed to the object itself.

        // for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {}

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage); // errors.put(error.getField(), error.getDefaultMessage())
        });

        //     String message = ex.getFieldErrors().stream()
        //        .map(e -> e.getDefaultMessage()).reduce(messageSource.getMessage("errors.found", null, locale), String::concat);

        // Map<String, String> results = new HashMap<>();
        // ex.getBindingResult().getAllErrors()
        //        .stream()
        //        .filter(FieldError.class::isInstance)
        //        .map(FieldError.class::cast)
        //        .forEach(fieldError -> results.put(fieldError.getField(), fieldError.getDefaultMessage()));

        // ref: https://www.baeldung.com/java-map-to-string-conversion
        // 1.
        //        StringBuilder mapAsString = new StringBuilder("{");
        //        for (String key : errors.keySet()) {
        //            mapAsString.append(key + "=" + errors.get(key) + ", ");
        //        }
        //        mapAsString.delete(mapAsString.length()-2, mapAsString.length()).append("}");
        //        System.out.println(mapAsString.toString());
        // 2.
        //        String mapAsString = errors.keySet().stream()
        //                .map(key -> key + "=" + errors.get(key)).collect(Collectors.joining(", ", "{", "}"));
        //        System.out.println(mapAsString.toString());
        //        Map<String, String> map = Arrays.stream(mapAsString.split(","))
        //                .map(entry -> entry.split("=")).collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));

        String mapAsString = errors.keySet().stream()
                .map(key -> key + "=" + errors.get(key)).collect(Collectors.joining(", ", "{", "}"));

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", messages.getMessage("exceptions.method-argument-not-valid-exception", new Object[]{mapAsString}, request.getLocale()));
        modelAndView.setViewName("views/error");
        return modelAndView;
    }

//        @ExceptionHandler({ Exception.class })
//        public ResponseEntity<Object> handleInternal(RuntimeException ex, WebRequest request) {
//        }

    // hisudoku domain specific exceptions:
    // EmailTakenException
    // NameTakenException
    // OperationFailedException
    // OperationNotAllowedException
    // SudokuNotFoundException
    // UserNotFoundException
    // EmailActivationTokenNotFound

    //basic exceptions:
    //	-BindException
    //	-MethodArgumentNotValidException
    //	-MissingServletRequestPartException
    //	-MissingServletRequestParameterException
    //	-ConstraintViolationException
    //	-TypeMismatchException
    //	-MethodArgumentTypeMismatchException
    //	-NoHandlerFoundException
    //	-HttpRequestMethodNotSupportedException
    //	-HttpMediaTypeNotSupportedException
}
