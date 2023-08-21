package io.playqd.mediaserver.api.rest.controller.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreateMediaSourceRequestValidator.class)
@Documented
public @interface ValidCreateMediaSourceRequest {

    String message() default "{sourcePath.isInvalid}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
