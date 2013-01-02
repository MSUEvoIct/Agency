package ec.agency.reflection;


import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;

import ec.agency.util.BadConfiguration;
import ec.agency.util.UnresolvableException;




public class RestrictedMethodDictionary implements MethodDictionary, Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	ArrayList<String>			_paths				= new ArrayList<String>();

	protected StimulusManager	_manager			= new StimulusManager();

	protected final Class<?>	_context_class;

	protected final int			_max_path;



	public RestrictedMethodDictionary(Class<?> cl, int max_path_length, Class<?>[] allowed_finals) {
		_context_class = cl;
		_manager.scanClass(cl);
		_max_path = max_path_length;
		_paths.addAll(StimulusManager.enumeratePaths(_manager, "", cl, _max_path, allowed_finals));
	}



	@Override
	public void addMethod(String name, Class<?> cl, Method m) throws BadConfiguration {
		_manager.addMethod(name, m, cl);
		_paths.addAll(StimulusManager.enumeratePaths(_manager, "", cl, _max_path));
	}



	public void removePath(String path) {
		_paths.remove(path);
	}



	public void removePaths(String[] paths) {
		for (String to_remove : paths) {
			_paths.remove(to_remove);
		}
	}



	public String[] enumerate() {
		return _paths.toArray(new String[_paths.size()]);
	}



	@Override
	public Object evaluate(String path, Object o) throws UnresolvableException {
		if (_paths.contains(path))
			return _manager.resolve(path, o, (Object[]) null);
		else
			throw new UnresolvableException(path + " is not a valid path in the dictionary.");
	}



	@Override
	public Object evaluate(String path, Object o, Object... args) throws UnresolvableException {
		if (_paths.contains(path))
			return _manager.resolve(path, o, args);
		else
			throw new UnresolvableException(path + " is not a valid path in the dictionary.");
	}
}
