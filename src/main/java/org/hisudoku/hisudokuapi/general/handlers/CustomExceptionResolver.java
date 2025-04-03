package org.hisudoku.hisudokuapi.general.handlers;

import org.springframework.context.MessageSource;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

import org.hisudoku.hisudokuapi.general.exceptions.*;

//DataFetcherExceptionResolver provides an async contract. However, in most cases, it is sufficient to extend DataFetcherExceptionResolverAdapter and override one of its resolveToSingleError or resolveToMultipleErrors methods that resolve exceptions synchronously.
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomExceptionResolver extends DataFetcherExceptionResolverAdapter {
    // The DataFetcherExceptionResolver chain hooks in as a GraphQL Java DataFetcherExceptionHandler. The Javadoc for that says it's "called when an exception is thrown during DataFetcher#get(DataFetchingEnvironment) execution". In other words, it can be used for field errors.

    private final MessageSource messages;

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
        Throwable t = NestedExceptionUtils.getMostSpecificCause(ex);

//        if (ex instanceof SudokuNotFoundException) {
        if (t instanceof SudokuNotFoundException) {
            return GraphqlErrorBuilder.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(messages.getMessage("exceptions.sudoku-not-found-exception", new Object[]{((SudokuNotFoundException) t).getSudokuId()}, env.getLocale()))
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .build();
        }

        if (t instanceof UserNotFoundException userNotFoundException) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.NOT_FOUND)
                    .message(messages.getMessage("exceptions.user-not-found-exception", null, env.getLocale()))
                    .build();
        }

        if (t instanceof EmailActivationTokenNotFound emailActivationTokenNotFound) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.NOT_FOUND)
                    .message(messages.getMessage("exceptions.email-activation-token-not-found-exception", new Object[]{((EmailActivationTokenNotFound) t).getToken()}, env.getLocale()))
                    .build();
        }

        if (t instanceof NameTakenException nameTakenException) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(messages.getMessage("exceptions.name-taken-exception", new Object[]{((NameTakenException) t).getUsername()}, env.getLocale()))
                    .build();
        }

        if (t instanceof EmailTakenException emailTakenException) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(messages.getMessage("exceptions.email-taken-exception", new Object[]{((EmailTakenException) t).getEmail()}, env.getLocale()))
                    .build();
        }

        if (t instanceof OperationNotAllowedException operationNotAllowedException) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.FORBIDDEN)
                    .message(messages.getMessage("exceptions.operation-not-allowed-exception", new Object[]{((OperationNotAllowedException) t).getOperation()}, env.getLocale()))
                    .build();
        }

        if (t instanceof OperationFailedException operationFailedException) {
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.INTERNAL_ERROR)
                    .message(messages.getMessage("exceptions.operation-failed-exception", new Object[]{((OperationFailedException) t).getReason()}, env.getLocale()))
                    .build();
        }

        if (t instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            Map<String, String> errors = new HashMap<>();

            methodArgumentNotValidException.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage); // errors.put(error.getField(), error.getDefaultMessage())
            });

            String mapAsString = errors.keySet().stream()
                    .map(key -> key + "=" + errors.get(key))
                    .collect(Collectors.joining(", ", "{", "}"));

//            System.out.println(mapAsString.toString());
//            Map<String, String> map = Arrays.stream(mapAsString.split(","))
//                    .map(entry -> entry.split("="))
//                    .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));

            // messages.getMessage("exceptions.method-argument-not-valid-exception", new Object[] {mapAsString}, env.getLocale())
            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(mapAsString)
                    .build();
        }

        if (t instanceof ConstraintViolationException constraintViolationException) {
            String invalidFields = constraintViolationException.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("\n"));

            return GraphqlErrorBuilder.newError(env)
                    .errorType(ErrorType.BAD_REQUEST)
//                    .message(messages.getMessage("exceptions.constraint-violation-exception", null, env.getLocale()))
                    .message(invalidFields)
                    .build();
        }

        log.debug("Error on gql endpoint, message - {}", t.getMessage());
        // other exceptions not yet caught
        return GraphqlErrorBuilder.newError(env)
                .message("Error occurred: Ensure request is valid ")
                .errorType(ErrorType.BAD_REQUEST)
                .build();
    }
}

