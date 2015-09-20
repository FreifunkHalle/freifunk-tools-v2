package freifunk.halle.tools.resource.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import freifunk.halle.tools.resource.validation.FormatValidation.FormatValidator;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FormatValidator.class)
public @interface FormatValidation {
	String message() default "";

	static enum FormatEnum {
		PNG, SVG, PDF, DOT, INPUT
	};

	public static class FormatValidator implements
			ConstraintValidator<IpListValidation, String> {

		@Override
		public void initialize(IpListValidation constraintAnnotation) {
		}

		@Override
		public boolean isValid(String value, ConstraintValidatorContext context) {
			try {
				FormatEnum.valueOf(value.toUpperCase());
			} catch (IllegalArgumentException ex) {
				return false;
			}
			return true;
		}
	}
}
