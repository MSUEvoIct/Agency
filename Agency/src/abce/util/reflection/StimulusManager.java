package abce.util.reflection;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import abce.util.BadConfiguration;
import abce.util.PathResolver;
import abce.util.Resolvable;
import abce.util.UnresolvableException;



/**
 * StimulusManager examines classes and stores information about the methods
 * tagged as Stimulus. It also is used to resolve and invoke method paths.
 * 
 * @author ruppmatt
 * 
 */
public class StimulusManager implements Resolvable {

	HashMap<Class<?>, ClassStimuli>	_descr			= new HashMap<Class<?>, ClassStimuli>();
	public final int				MAX_STACK_DEPTH	= 10;



	public StimulusManager() {
	}



	/**
	 * Given the name of a resource, recursively collect all methods tagged as a
	 * stimulus.
	 * 
	 * @param res
	 *            Path of resource
	 * @throws UnresolvableException
	 */
	public void scanResource(String res) throws UnresolvableException {
		ClassLoader ctx = Thread.currentThread().getContextClassLoader();
		try {
			Enumeration<URL> url = ctx.getResources(res.replace('.', '/'));
			ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
			while (url.hasMoreElements()) {
				File f = new File(url.nextElement().getFile());
				classes.addAll(findClasses(f, res));
			}
		} catch (IOException e) {
			throw new UnresolvableException("Unable to locate requested resource for annotation check.");
		}

	}



	/**
	 * Given a class, recursively collect all methods tagged as a stimulus.
	 * 
	 * @param cl
	 *            Class to begin scan
	 */
	public void scanClass(Class<?> cl) {
		Stack<Class<?>> to_examine = new Stack<Class<?>>();
		to_examine.add(cl);
		while (!to_examine.isEmpty()) {
			Class<?> cur = to_examine.pop();
			if (!_descr.containsKey(cur)) {
				ClassStimuli d = describeClass(cur);
				_descr.put(cur, d);
				if (d.size() > 0) {
					for (Member m : d.getMembers()) {
						Class<?> to_add = (d.stimuliUsesAssistant(m)) ? d.getAssistant(m) : getType(m);
						if (!_descr.containsKey(to_add) && !to_examine.contains(to_add)) {
							to_examine.add(to_add);
						}
					}
				}
			}

		}
	}



	/**
	 * Manually add a method to the manager
	 * 
	 * @param name
	 *            name of method
	 * @param m
	 *            method
	 * @param cl
	 *            class the method should be associated with
	 * @throws BadConfiguration
	 */
	public void addMethod(String name, Member m, Class<?> cl) throws BadConfiguration {
		if (_descr.containsKey(cl)) {
			_descr.put(cl, new ClassStimuli());
		}
		_descr.get(cl).add(m);
	}



	/**
	 * Retrieve the stimuli for a class. This does not look for new stimuli, but
	 * only returns what is already in the manager.
	 * 
	 * @param cl
	 *            Class to retrieve stimuli for
	 * @return
	 */
	public ClassStimuli getStimuliForClass(Class<?> cl) {
		if (_descr.containsKey(cl)) {
			return _descr.get(cl);
		} else {
			return null;
		}
	}



	/**
	 * Returns true if the class has been examined by the manager for Stimuli
	 * objects.
	 * 
	 * @param cl
	 * @return
	 */
	public boolean knownClass(Class<?> cl) {
		return _descr.containsKey(cl);
	}



	/**
	 * Write information about the known Stimuli for a class.
	 * 
	 * @param cl
	 *            Class to display information for
	 * @return
	 */
	public String writeDescriptions(Class<?> cl) {

		StringBuffer buf = new StringBuffer();
		if (_descr.containsKey(cl)) {
			ClassStimuli dm = _descr.get(cl);
			for (Member m : dm.getMembers()) {
				Stimulus desc = dm.getDescriptor(m);
				buf.append(cl.getName().toString() + ",");
				buf.append(m.getDeclaringClass().getName().toString() + ":" + m.getName() + ",");
				if (m instanceof Method) {
					buf.append("METHOD,");
				} else {
					buf.append("FIELD,");
				}
				buf.append(getType(m).getName().toString() + ",");
				buf.append(desc.name() + ",");
				buf.append(desc.assistant().getName().toString() + ",");
			}
		}
		return buf.toString();
	}



	/**
	 * Examine all known paths beginning with a single class.
	 * 
	 * @param cl
	 *            Class to start search
	 * @return
	 */
	public String writeDescriptionWeb(Class<?> cl) {
		;
		HashSet<Class<?>> visited = new HashSet<Class<?>>();
		Stack<Class<?>> stack = new Stack<Class<?>>();
		StringBuffer buf = new StringBuffer();
		stack.add(cl);
		while (!stack.empty()) {
			Class<?> cur = stack.pop();
			if (visited.contains(cur)) {
				continue;
			} else {
				visited.add(cur);
			}
			ClassStimuli dm = _descr.get(cur);

			for (Member m : dm.getMembers()) {
				Stimulus desc = dm.getDescriptor(m);
				Class<?> return_type = getType(m);
				Class<?> assistant = desc.assistant();
				buf.append(cl.getName().toString() + ", ");
				buf.append(m.getDeclaringClass().getName().toString() + ":" + m.getName() + ", ");
				buf.append(return_type.getName().toString() + ", ");
				buf.append(desc.name() + ", ");
				buf.append(assistant.getName().toString() + "\n");
				if (!visited.contains(return_type) && !stack.contains(return_type)) {
					stack.add(return_type);
				}
			}
		}
		return buf.toString();
	}



	/**
	 * Given an object, return all associated stimuli
	 * 
	 * @param o
	 * @return
	 */
	public String[] getStimuli(Object o) {
		Class<?> cl = o.getClass();
		if (_descr.containsKey(cl))
			return _descr.get(cl).getNames();
		else
			return new String[0];
	}



	/**
	 * Public method to begin resolution of a method path.
	 * 
	 * @param path
	 *            path to resolve
	 * @param ctx
	 *            root object of resolution
	 * @params params
	 *         parameters to begin resolution
	 */
	@Override
	public Object resolve(String path, Object ctx, Object... params) throws UnresolvableException {
		return resolve(0, path, ctx, params);
	}



	/**
	 * Internal method (called recursively) to resolve path
	 * // Class objects passed in as context are treated as assistants
	 * // TODO: This may be deprecated; assistants where originally envisioned
	 * // to be classes containing only static members; however any sort of
	 * // generics (e.g. to handle collections by iterating over them) requires
	 * // non-static methods and a call to at least an object constructor. The
	 * // resolution method below, in fact, makes this assumption.
	 * 
	 * @param depth
	 *            the current depth of the resolution
	 * @param path
	 *            the remaining path to resolve
	 * @param ctx
	 *            the next object to invoke a method upon
	 * @param params
	 *            the parameters used to invoke the next method
	 * @return
	 * @throws UnresolvableException
	 */
	protected Object resolve(int depth, String path, Object ctx, Object... params) throws UnresolvableException {
		if (depth > MAX_STACK_DEPTH) {
			throw new UnresolvableException("Maximum resolution stack depth reached.  Remaining: " + path);
		}

		String prefix = PathResolver.getPrefix(path);
		String name = (prefix == null) ? path : prefix;
		Class<?> cl_lookup = (ctx instanceof Class<?>) ? (Class<?>) ctx : ctx.getClass();

		if (_descr.containsKey(cl_lookup)) {
			ClassStimuli stimulus = _descr.get(cl_lookup);
			if (stimulus.containsName(name)) {
				Member m = stimulus.get(name);
				Object invoker = (ctx instanceof Class<?>) ? null : ctx;

				// Retrieve the object represented by the current name
				Object retrieved = null;
				try {
					if (m instanceof Method) {
						retrieved = ((Method) m).invoke(invoker, params);
					} else {
						retrieved = ((Field) m).get(invoker);
					}
				} catch (Exception e) {
					throw new UnresolvableException("Cannot retrieve member value for name " + name
							+ " in object of class " + cl_lookup.getName());
				}

				if (prefix == null) { // There is nothing else to resolve
					return retrieved;
				} else {
					if (stimulus.stimuliUsesAssistant(m)) {
						Object assistant_obj;
						try {
							assistant_obj = stimulus.getAssistant(m).getConstructor((Class<?>[]) null)
									.newInstance((Object[]) null);
						} catch (Exception e) {
							throw new UnresolvableException("Cannot instantiate assistant of type "
									+ stimulus.getAssistant(m).getName());
						}
						return resolve(depth + 1, PathResolver.extractPrefix(path), assistant_obj, retrieved);
					} else {
						return resolve(depth + 1, PathResolver.extractPrefix(path), retrieved);
					}
				}

			} else {
				throw new UnresolvableException("Cannot locate member for name " + name + " in object of class "
						+ cl_lookup.getName());
			}
		} else {
			throw new UnresolvableException("There are no descriptors for invocation of or class named "
					+ cl_lookup.getName());
		}
	}



	/**
	 * Get the stimuli in a class; try to add them
	 * 
	 * @param cl
	 * @return
	 */
	public ClassStimuli describeClass(Class<?> cl) {
		ClassStimuli dm = new ClassStimuli();
		for (Method m : cl.getMethods()) {
			try {
				dm.add(m);
			} catch (BadConfiguration bc) {
			}
		}
		for (Field f : cl.getFields()) {
			try {
				dm.add(f);
			} catch (BadConfiguration bc) {
			}
		}
		return dm;
	}



	/**
	 * Used internally to find resource files (.class) recursively
	 * 
	 * @param f
	 *            File to examine
	 * @param res
	 *            Name of resource
	 * @return
	 *         List of classes collected
	 * @throws UnresolvableException
	 */
	protected ArrayList<Class<?>> findClasses(File f, String res) throws UnresolvableException {
		ArrayList<Class<?>> cl = new ArrayList<Class<?>>();
		if (!f.exists()) {
			return cl;
		} else if (!f.isDirectory() && f.getName().endsWith(".class")) {
			cl.add(getClass(res + "." + f.getName()));
		} else if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (File df : files) {
				cl.addAll(findClasses(df, res + "." + f.getName()));
			}
		}
		return cl;
	}



	/**
	 * Given a class name, try to return the class
	 * 
	 * @param path
	 *            path to class
	 * @return
	 * @throws UnresolvableException
	 */
	protected Class<?> getClass(String path) throws UnresolvableException {
		Class<?> retval = null;
		try {
			retval = Class.forName(path);
		} catch (ClassNotFoundException e) {
			throw new UnresolvableException("Unable to find class " + path);
		}
		return retval;
	}



	/**
	 * Given a member, return the returnType if it is a method or its type if it
	 * is a field
	 * 
	 * @param m
	 *            Member to get the type of
	 * @return
	 */
	public static Class<?> getType(Member m) {
		if (m instanceof Method) {
			return ((Method) m).getReturnType();
		} else {
			return ((Field) m).getType();
		}
	}



	/**
	 * Static method to enumerate the paths associated with a class using a
	 * particular StimulusManager
	 * 
	 * @param manager
	 *            Manager to use
	 * @param prefix
	 *            Current prefix of path
	 * @param lookup
	 *            Current class to perform the lookup on
	 * @param remain
	 *            The length of the path remaining to resolve
	 * @return
	 */
	public static ArrayList<String> enumeratePaths(StimulusManager manager, String prefix, Class<?> lookup, int remain) {
		ArrayList<String> retval = new ArrayList<String>();
		ClassStimuli desc = manager.getStimuliForClass(lookup);
		String sep = (prefix.equals("")) ? "" : ".";
		if (desc == null) {
			return retval;
		} else {
			for (Member m : desc.getMembers()) {
				String name = desc.getName(m);
				String path = prefix + sep + name;
				if (!desc.stimuliUsesAssistant(m))
					retval.add(path);
				Class<?> nextLookup = (desc.stimuliUsesAssistant(m)) ? desc.getAssistant(m) : getType(m);
				if (remain == 1) {
					continue;
				}
				retval.addAll(StimulusManager.enumeratePaths(manager, path, nextLookup, remain - 1));
			}
		}
		return retval;
	}



	/**
	 * Static method to enumerate the paths associated with a class using a
	 * particular StimulusManager
	 * 
	 * @param manager
	 *            Manager to use
	 * @param prefix
	 *            Current prefix of path
	 * @param lookup
	 *            Current class to perform the lookup on
	 * @param remain
	 *            The length of the path remaining to resolve
	 * @return
	 */
	public static ArrayList<String> enumeratePaths(StimulusManager manager, String prefix, Class<?> lookup, int remain,
			Class<?>[] allowed_finals) {
		ArrayList<String> retval = new ArrayList<String>();
		ClassStimuli desc = manager.getStimuliForClass(lookup);
		String sep = (prefix.equals("")) ? "" : ".";
		if (desc == null) {
			return retval;
		} else {
			for (Member m : desc.getMembers()) {
				String name = desc.getName(m);
				String path = prefix + sep + name;
				if (!desc.stimuliUsesAssistant(m) && containsClass(getType(m), allowed_finals))
					retval.add(path);
				Class<?> nextLookup = (desc.stimuliUsesAssistant(m)) ? desc.getAssistant(m) : getType(m);
				if (remain == 1) {
					continue;
				}
				retval.addAll(StimulusManager.enumeratePaths(manager, path, nextLookup, remain - 1, allowed_finals));
			}
		}
		return retval;
	}



	protected static boolean containsClass(Class<?> lookup, Class<?>[] possible) {
		for (Class<?> cl : possible) {
			if (cl.equals(lookup))
				return true;
		}
		return false;
	}
}
