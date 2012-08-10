package abce.util.reflection;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Stimulus {

	String name();



	Class<?> assistant() default NullAssistant.class;



	boolean ec_default() default true;



	boolean policy_default() default true;
}
