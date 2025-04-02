package org.hisudoku.hisudokuapi.general.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.PropertyResolver;

public class ConfigurableSizeCharSequenceValidator implements ConstraintValidator<ConfigurableSize, CharSequence> {
    private final PropertyResolver propertyResolver;
    private int min;
    private int max;
    private String fieldName;

    @Autowired
    public ConfigurableSizeCharSequenceValidator(PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    @Override
    public void initialize(ConfigurableSize configurableSize) {
        String minProperty = configurableSize.minProperty();
        String maxProperty = configurableSize.maxProperty();
        this.fieldName = configurableSize.fieldName();
        this.min = "".equals(minProperty) ? 0 : propertyResolver.getRequiredProperty(minProperty, Integer.class);
        this.max = "".equals(maxProperty) ? Integer.MAX_VALUE : propertyResolver.getRequiredProperty(maxProperty, Integer.class);
        validateParameters();
    }

    private void validateParameters() {
        if (this.min < 0) {
            throw new IllegalArgumentException("The min parameter cannot be negative.");
        } else if (this.max < 0) {
            throw new IllegalArgumentException("The max parameter cannot be negative.");
        } else if (this.max < this.min) {
            throw new IllegalArgumentException("The length cannot be negative.");
        }
    }

    // The isValid(…) method performs the validation logic. If the value is ok, it retrieves localized messages for the attribute field and message corresponding to the current locale from the request context. The attribute message is interpolated to form a complete message.
    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        } else {
            int length = value.length();
            boolean retVal = length >= this.min && length <= this.max;
            if (!retVal) {
                HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
                hibernateContext.addMessageParameter("min", this.min).addMessageParameter("max", this.max).addMessageParameter("fieldName", this.fieldName);
                hibernateContext.disableDefaultConstraintViolation();
                hibernateContext
//                        .buildConstraintViolationWithTemplate("{javax.validation.constraints.Size.message}")
//                        .buildConstraintViolationWithTemplate("{jakarta.validation.constraints.Size.message}")
                        .buildConstraintViolationWithTemplate("{validation.configurable-size-char-sequence-validator}")
                        .addConstraintViolation();
            }
            return retVal;
        }
    }
}

//-------------------------------------------------------------------------------------------------------------------
// There is the problem in the default MessageInterpolator. It translates the placeholder for one time only. We need to apply the interpolation to the message again to replace the subsequent placeholder with the localized message. In this case, we have to define a custom message interpolator to replace the default one:
//public class RecursiveLocaleContextMessageInterpolator extends AbstractMessageInterpolator {
//    private static final Pattern PATTERN_PLACEHOLDER = Pattern.compile("\\{([^}]+)\\}");
//    private final MessageInterpolator interpolator;
//
//    public RecursiveLocaleContextMessageInterpolator(ResourceBundleMessageInterpolator interpolator) {
//        this.interpolator = interpolator;
//    }
//    @Override
//    public String interpolate(MessageInterpolator.Context context, Locale locale, String message) {
//        int level = 0;
//        while (containsPlaceholder(message) && (level++ < 2)) {
//            message = this.interpolator.interpolate(message, context, locale);
//        }
//        return message;
//    }
//    private boolean containsPlaceholder(String code) {
//        Matcher matcher = PATTERN_PLACEHOLDER.matcher(code);
//        return matcher.find();
//    }
//}
//
// It reapplies interpolation with the wrapped MessageInterpolator when it detects the message contains any curly bracket placeholder. We’ve completed the implementation, and it’s time for us to configure Spring Boot to incorporate it.
//@Bean
//public MessageInterpolator getMessageInterpolator(MessageSource messageSource) {
//    MessageSourceResourceBundleLocator resourceBundleLocator = new MessageSourceResourceBundleLocator(messageSource);
//    ResourceBundleMessageInterpolator messageInterpolator = new ResourceBundleMessageInterpolator(resourceBundleLocator);
//    return new RecursiveLocaleContextMessageInterpolator(messageInterpolator);
//}
//@Bean
//public LocalValidatorFactoryBean getValidator(MessageInterpolator messageInterpolator) {
//    LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
//    bean.setMessageInterpolator(messageInterpolator);
//    return bean;
//}
//The getMessageInterpolator(…) method returns our own implementation. This implementation wraps ResourceBundleMessageInterpolator, which is the default MessageInterpolator in Spring Boot. The getValidator() is for registering the validator to use our customized MessageInterpolator within our web service. Now, we’re all set, and let’s test it once more. We’ll have the following complete interpolated message with the placeholder replaced by the localized message as well
