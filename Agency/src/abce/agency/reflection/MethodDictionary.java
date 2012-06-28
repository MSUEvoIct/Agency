package abce.agency.reflection;

import java.lang.reflect.Method;

import abce.agency.util.BadConfiguration;
import abce.agency.util.UnresolvableException;


public interface MethodDictionary {

	public void addMethod(String name, Class<?> cl, Method m) throws BadConfiguration;



	public Object evaluate(String path, Object o) throws UnresolvableException;



	public Object evaluate(String path, Object o, Object... args) throws UnresolvableException;

}
