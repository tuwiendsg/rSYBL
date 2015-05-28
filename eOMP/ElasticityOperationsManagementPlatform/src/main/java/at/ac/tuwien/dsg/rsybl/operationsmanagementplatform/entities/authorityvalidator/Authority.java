package at.ac.tuwien.dsg.rsybl.operationsmanagementplatform.entities.authorityvalidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AuthorityValidator.class)
public @interface Authority {

    int min();

    int max();

    String message() default "The authority is not between admissible boundaries";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
