package abce.util.reflection;

import java.lang.reflect.Method;

import abce.util.BadConfiguration;
import abce.util.UnresolvableException;


public interface MethodDictionary {

	public void addMethod(String name, Class<?> cl, Method m) throws BadConfiguration;



	public Object evaluate(String path, Object o) throws UnresolvableException;



	public Object evaluate(String path, Object o, Object... args) throws UnresolvableException;

}
