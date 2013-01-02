package ec.agency.util;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;



public class FitnessFactory {

	@SuppressWarnings("unchecked")
	public static FitnessFunction findFitnessFunc(String name, Object o) throws UnresolvableException {
		Class<?> cl = o.getClass();
		for (Class<?> in : cl.getClasses()) {
			for (Annotation a : in.getAnnotations()) {
				if (a instanceof NamedFitness) {
					if (((NamedFitness) a).name().equals(name)) {
						Constructor<FitnessFunction>[] constrs = (Constructor<FitnessFunction>[]) cl.getConstructors();
						Constructor<FitnessFunction> useThis = null;

						// Try to find a constructor that takes o as an argument
						for (Constructor<FitnessFunction> con : constrs) {
							Class<?>[] params = con.getParameterTypes();
							if (params.length == 1 && params[0].isAssignableFrom(o.getClass())) {
								useThis = con;
								break;
							}
						}

						// If a constructor that takes o as an argument is not
						// found, locate a null-parameter constructor
						if (useThis == null) {
							try {
								useThis = (Constructor<FitnessFunction>) cl.getConstructor((Class<?>[]) null);
								return useThis.newInstance((Object[]) null);
							} catch (Exception e) {
								throw new UnresolvableException("Unable to construct " + useThis.getName()
										+ " as fitness function named " + name + " on object of type " + o.getClass());
							}
						} else { // Constructor with o paramter found, try to
									// invoke it
							try {
								useThis.newInstance(o);
							} catch (Exception e) {
								throw new UnresolvableException("Unable to find fitness function named " + name
										+ " for object of type " + o.getClass());
							}
						}
					}
				}
			}
		}
		// No fitness function with the specified name was found.
		throw new UnresolvableException("Unable to find fitness function named " + name + " for object of type "
				+ o.getClass());
	}
}
