package ec.agency.reflection;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import ec.agency.util.BadConfiguration;




public class ResponseUtils {

	/**
	 * Find method call that is annotated as a Response
	 */

	public static Method findResponse(Object context) throws BadConfiguration {
		Class<?> cl = context.getClass();
		for (Method m : cl.getMethods()) {
			for (Annotation a : m.getAnnotations()) {
				if (a instanceof Response) {
					return m;
				}
			}
		}
		throw new BadConfiguration("Unable to locate a valid response method in class " + context.getClass().getName());
	}



	/**
	 * Invoke a method
	 * 
	 * @param actor
	 *            Invoking object
	 * @param method
	 *            Method to call
	 * @param params
	 *            Parameters for method
	 * @throws BadConfiguration
	 */
	public static void doMethod(Object actor, Method method, Object[] params) throws BadConfiguration {
		try {
			method.invoke(actor, params);
		} catch (Exception e) {
			BadConfiguration bc = new BadConfiguration(e.getMessage());
			bc.append("Unable to invoke method " + method.getName() + " on actor " + actor.getClass().getName());
			throw bc;
		}
	}

}
