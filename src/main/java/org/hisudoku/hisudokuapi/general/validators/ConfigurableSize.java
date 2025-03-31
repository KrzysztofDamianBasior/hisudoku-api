package org.hisudoku.hisudokuapi.general.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ConfigurableSize.List.class) // Repeating Annotations imply that a particular annotation is applied multiple times to a declaration, Java 8 onwards Repeating Annotations are allowed, but we need to define an annotation in a specific way to make it repeatable. We need to create an annotation which has an attribute which is an array of the Repeating Annotation type. Class.java, from Java 8 onwards, supports a new method getAnnotationsByType(<Annotation-class>) method which returns an array of Annotation-Class type.
@Constraint(validatedBy = {ConfigurableSizeCharSequenceValidator.class})
public @interface ConfigurableSize {

    String message() default "size is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String minProperty() default ""; // default "{application.validation.min-username-length}";

    String maxProperty() default ""; // default "{application.validation.max-username-length}";

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented // @Documented is a meta-annotation. You apply @Documented when defining an annotation, to ensure that classes using your annotation show this in their generated JavaDoc.
    @interface List {
        ConfigurableSize[] value();
    }
}