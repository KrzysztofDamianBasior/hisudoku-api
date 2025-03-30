package org.hisudoku.hisudokuapi.general.configs;

import graphql.schema.GraphQLScalarType;

import org.hisudoku.hisudokuapi.general.datatypes.LocalDateScalar;
import org.hisudoku.hisudokuapi.general.datatypes.LocalDateTimeScalar;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLScalarConfiguration {
    @Bean
    public GraphQLScalarType localDateTimeScalar() {
        return LocalDateTimeScalar.createLocalDateTimeScalar();
    }
    @Bean
    public GraphQLScalarType localDateScalar() {
        return LocalDateScalar.createLocalDateScalar();
    }

    // ref: https://medium.com/@dev.jefster/enhancing-graphql-with-custom-scalars-for-date-and-time-in-spring-boot-316747b731af
    // https://docs.spring.io/spring-graphql/docs/1.1.0-RC1/reference/html/#execution-graphqlsource-runtimewiring-configurer
    @Bean
    RuntimeWiringConfigurer runtimeWiringConfigurer() {
        GraphQLScalarType localDateScalarType = localDateScalar();
        GraphQLScalarType localDateTimeScalarType = localDateTimeScalar();
        return wiringBuilder -> wiringBuilder
                .scalar(localDateScalarType)
                .scalar(localDateTimeScalarType);
    }

/*
    // https://docs.spring.io/spring-graphql/docs/1.1.0-RC1/reference/html/#execution-graphqlsource
    @Bean
    public GraphQlSourceBuilderCustomizer sourceBuilderCustomizer() {
        GraphQLScalarType scalarType = dateScalar();
        return (builder) ->
                builder.configureGraphQl(graphQlBuilder ->
                        graphQlBuilder.executionIdProvider((query, operationName, context) -> null));
    }
*/
}