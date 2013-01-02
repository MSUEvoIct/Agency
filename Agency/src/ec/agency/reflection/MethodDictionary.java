package ec.agency.reflection;

import java.lang.reflect.Method;

import ec.agency.util.BadConfiguration;
import ec.agency.util.UnresolvableException;



public interface MethodDictionary {

	public void addMethod(String name, Class<?> cl, Method m) throws BadConfiguration;



	public Object evaluate(String path, Object o) throws UnresolvableException;



	public Object evaluate(String path, Object o, Object... args) throws UnresolvableException;

}
