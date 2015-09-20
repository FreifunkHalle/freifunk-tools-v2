package freifunk.halle.tools.resource.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import freifunk.halle.tools.resource.validation.IpListValidation.IpListValidator;
import freifunk.halle.tools.types.NetTools;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IpListValidator.class)
public @interface IpListValidation {
	String message() default "";

	public static class IpListValidator implements
			ConstraintValidator<IpListValidation, String> {

		private NetTools _netTools;

		@Autowired
		public IpListValidator(NetTools netTools) {
			_netTools = netTools;
		}

		@Override
		public void initialize(IpListValidation constraintAnnotation) {
		}

		@Override
		public boolean isValid(String value, ConstraintValidatorContext context) {

			Iterable<String> split = Splitter.on(Pattern.compile("\\||,"))
					.split(value);
			for (String singleSplit : split) {
				Pattern pattern = Pattern.compile("([1-9]\\d{0,2}[\\.]?){1,4}");
				if (!pattern.matcher(singleSplit).matches()) {
					return false;
				}
			}
			return true;
		}

		public static List<InetAddress> parse(String value)
				throws UnknownHostException {
			Iterable<String> split = Splitter.on(Pattern.compile("\\||,"))
					.split(value);
			List<InetAddress> result = Lists.newArrayList();
			for (String singleSplit : split) {
				result.add(InetAddress.getByName(singleSplit));
			}
			return result;
		}
	}
}