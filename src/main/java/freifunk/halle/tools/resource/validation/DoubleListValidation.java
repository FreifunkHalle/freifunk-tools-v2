package freifunk.halle.tools.resource.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import freifunk.halle.tools.resource.validation.DoubleListValidation.DoubleListValidator;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DoubleListValidator.class)
public @interface DoubleListValidation {
	String message() default "";

	public static class DoubleListValidator implements
			ConstraintValidator<DoubleListValidation, String> {

		@Override
		public void initialize(DoubleListValidation constraintAnnotation) {
		}

		@Override
		public boolean isValid(String value, ConstraintValidatorContext context) {

			Iterable<String> split = Splitter.on(Pattern.compile("\\|")).split(
					value);
			if (Iterables.size(split) <= 4) {
				try {
					for (String singleSplit : split) {
						Double.parseDouble(singleSplit);
					}
				} catch (NumberFormatException ex) {
					return false;
				}
				return true;
			}

			return false;
		}

		public static List<Double> parse(String value) {
			List<Double> result = Lists.newArrayList();
			Iterable<String> split = Splitter.on(Pattern.compile("\\|")).split(
					value);
			for (String singleSplit : split) {
				result.add(Double.parseDouble(singleSplit));
			}
			return result;
		}
	}

}