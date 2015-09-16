package freifunk.halle.tools.resource.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import freifunk.halle.tools.resource.validation.Format.FormatValidation;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FormatValidation.class)
public @interface Format {
	String message() default "";

	enum FormatEnum {
		PNG, SVG, PDF, DOT, INPUT
	};

	public static class FormatValidation implements ConstraintValidator<Format, String> {

		@Override
		public void initialize(Format constraintAnnotation) {
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
