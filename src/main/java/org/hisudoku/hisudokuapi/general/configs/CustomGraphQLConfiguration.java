package org.hisudoku.hisudokuapi.general.configs;

import graphql.analysis.MaxQueryComplexityInstrumentation;
import graphql.analysis.MaxQueryDepthInstrumentation;
//import graphql.analysis.FieldComplexityCalculator;
//import graphql.analysis.FieldComplexityEnvironment;
//import graphql.language.IntValue;
//import graphql.schema.GraphQLDirective;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//One of the important (security) aspects to consider when developing a GraphQL server, is to make sure clients will not exhaust it by querying too many levels or fields. E.g. when having a circular model a bad client could request many many levels deep almost causing an infinite loop. So a good practice is:
//    - to have a limit on the length of the GraphQL query passed to the engine itself
//    - to have a limit on query depth
//    - to have a limit on query complexity (number of fields requested)

@Configuration
public class CustomGraphQLConfiguration {
    // GraphQL supports nested objects as a response, but sometimes a restriction is required to limit the depth of the response. Spring Boot GraphQL provides inbuild MaxQueryDepthInstrumentation that adds a limit to the nested query and throws an error if the query is greater than the specified values.
    //    @Bean
    //    MaxQueryDepthInstrumentation maxQueryDepthInstrumentation() {
    //        return new MaxQueryDepthInstrumentation(8);
    //    }

    @Bean
    @ConditionalOnMissingBean // It will initialize the bean only when it cannot find a MaxQueryDepthInstrumentation bean in its context. If it already has a MaxQueryDepthInstrumentation bean, it will skip this. The @ConditionalOnMissingBean annotation is used to load a bean only if a given bean is missing. This annotation provides flexibility in bean creation, allowing you to override default implementations or provide alternative implementations based on the absence of a bean.
    @ConditionalOnProperty(prefix = "spring.graphql.instrumentation", name = "max-query-depth")
    public MaxQueryDepthInstrumentation maxQueryDepthInstrumentation(@Value("${spring.graphql.instrumentation.max-query-depth}") int maxDepth) {
        return new MaxQueryDepthInstrumentation(maxDepth);
    }

    //    @ConditionalOnProperty(
    //            name = "feature.myFeature.enabled",
    //            havingValue = "true",
    //            matchIfMissing = false
    //    )
    //
    //    feature.myFeature.enabled=true
    //    @ConditionalOnProperty(name = "feature.myFeature.enabled", havingValue = "true")
    //
    //    name: This is the key of the property that Spring will look for in the application properties file (e.g., application.properties or application.yml). In this case, it's "feature.myFeature.enabled".
    //    havingValue: This specifies the value that the property must have for the bean to be instantiated. In this example, the bean myFeatureBean() will only be instantiated if the property feature.myFeature.enabled is set to "true".
    //    matchIfMissing: This is a boolean flag that controls what happens if the property is not defined in the properties file. If matchIfMissing is true, the bean will be instantiated even if the property is absent. If false (as in the example above), the bean will only be created if the property exists and has the value "true".

    // https://documentation.coremedia.com/cmcc-10/current/webhelp/headlessserver-en/content/querycomplexity.html
    //  The higher the complexity of a query is, the higher is the resulting potential load on the server. The complexity of a query may be limited by a MaxQueryComplexityInstrumentation which is provided by the graphql-java framework. By default, the complexity of a query is calculated by summing up the number of requested fields and nested levels. A more sophisticated complexity calculator may be added to the Spring configuration by implementing the FieldComplexityCalculator interface from graphql-java. Like the query depth, the complexity of a query is calculated before actually invoking the query. The complexity limit can be enabled by setting the configuration property caas.graphql.max-query-complexity to a value greater than 0. The default is 0 which means that this check is disabled.
    //    @Bean
    //    @ConditionalOnMissingBean
    //    @ConditionalOnProperty(value = "graphql.servlet.max-query-complexity")
    //    public MaxQueryComplexityInstrumentation maxQueryComplexityInstrumentation() {
    //        return new MaxQueryComplexityInstrumentation(maxQueryComplexity);
    //    }
    //  @Bean
    //  public MaxQueryComplexityInstrumentation maxQueryComplexityInstrumentation() {
    //    return new MaxQueryComplexityInstrumentation(10);
    //  }
    // The configuration property graphql.servlet.max-query-complexity works like a charm with servlets but does not have any effect with WebFlux. The instrumentation class MaxQueryComplexityInstrumentation implements the graph complexity functionality and is added by the GraphQLInstrumentationAutoConfiguration

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.graphql.instrumentation", name = "max-query-complexity")
    public MaxQueryComplexityInstrumentation maxQueryComplexityInstrumentation(@Value("${spring.graphql.instrumentation.max-query-complexity}") int maxComplexity) {
        return new MaxQueryComplexityInstrumentation(maxComplexity);
    }

//    ----------------------------------------------------------------------------
//    class CustomFieldComplexityCalculator implements FieldComplexityCalculator {
//        @Override
//        public int calculate(FieldComplexityEnvironment environment, int childComplexity) {
//            GraphQLDirective complexity = environment.getFieldDefinition().getDirective("Complexity");
//            if (complexity != null) {
//                IntValue value = complexity
//                        .getArgument("complexity")
//                        .toAppliedArgument()
//                        .getValue();
//
//                return value.getValue().intValue() + childComplexity;
//            }
//            return 1 + childComplexity;
//        }
//    }
//      @Bean
//      public MaxQueryComplexityInstrumentation maxQueryComplexityInstrumentation() {
//        return new MaxQueryComplexityInstrumentation(50, new CustomFieldComplexityCalculator());
//      }
}
