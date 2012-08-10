package abce.util.reflection;


import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;

import abce.util.BadConfiguration;
import abce.util.UnresolvableException;
import ec.util.MersenneTwisterFast;



public class UnrestrictedMethodDictionary implements MethodDictionary, Serializable {

	private static final long	serialVersionUID	= 1L;
	protected StimulusManager	_manager			= new StimulusManager();
	protected final Class<?>	_context_class;



	public UnrestrictedMethodDictionary(Class<?> cl) {
		_context_class = cl;
		_manager.scanClass(cl);
	}



	@Override
	public void addMethod(String name, Class<?> cl, Method m) throws BadConfiguration {
		_manager.addMethod(name, m, cl);
	}



	@Override
	public Object evaluate(String path, Object o) throws UnresolvableException {
		return evaluate(path, o, (Object[]) null);
	}



	@Override
	public Object evaluate(String path, Object o, Object... args) throws UnresolvableException {
		return _manager.resolve(path, o, args);
	}



	public String getRandomPath(MersenneTwisterFast gen, int max_length) throws BadConfiguration {
		Class<?> lookup = _context_class;
		String prefix = "";
		ArrayList<String> paths = StimulusManager.enumeratePaths(_manager, prefix, lookup, max_length);
		int ndx = gen.nextInt(paths.size());
		return paths.get(ndx);
	}



	public ArrayList<String> enumeratePaths(int max_length) {
		return StimulusManager.enumeratePaths(_manager, "", _context_class, max_length);
	}

}
