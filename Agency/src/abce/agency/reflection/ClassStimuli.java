package abce.agency.reflection;


import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.util.LinkedHashMap;

import abce.agency.util.BadConfiguration;



public class ClassStimuli implements Serializable {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;
	LinkedHashMap<String, Member>	_str2mem			= new LinkedHashMap<String, Member>();
	LinkedHashMap<Member, Stimulus>	_mem2desc			= new LinkedHashMap<Member, Stimulus>();



	public ClassStimuli() {
	}



	public boolean containsName(String name) {
		return (_str2mem.containsKey(name));
	}



	public boolean stimuliUsesAssistant(Member m) {
		if (_mem2desc.containsKey(m)) {
			return !_mem2desc.get(m).assistant().equals(NullAssistant.class);
		} else {
			return false;
		}
	}



	public Member get(String name) {
		if (_str2mem.containsKey(name)) {
			return _str2mem.get(name);
		} else {
			return null;
		}
	}



	public Class<?> getAssistant(Member m) {
		if (_mem2desc.containsKey(m)) {
			return _mem2desc.get(m).assistant();
		} else {
			return null;
		}
	}



	public Stimulus getDescriptor(Member m) {
		if (_mem2desc.containsKey(m)) {
			return _mem2desc.get(m);
		} else {
			return null;
		}
	}



	public Stimulus getDescriptor(String name) {
		if (_str2mem.containsKey(name)) {
			return _mem2desc.get(_str2mem.get(name));
		} else {
			return null;
		}
	}



	public Member[] getMembers() {
		return _mem2desc.keySet().toArray(new Member[_mem2desc.size()]);
	}



	public String[] getNames() {
		return _str2mem.keySet().toArray(new String[_str2mem.size()]);
	}



	public String getName(Member m) {
		if (_mem2desc.containsKey(m)) {
			return _mem2desc.get(m).name();
		} else {
			return null;
		}
	}



	public int size() {
		return _mem2desc.size() + _mem2desc.size();
	}



	public void add(Member m) throws BadConfiguration {
		Stimulus desc = ((AccessibleObject) m).getAnnotation(Stimulus.class);
		if (desc == null) {
			throw new BadConfiguration("Method requires descriptor.  Unable to add method: " + m.getName());
		} else if (_mem2desc.containsKey(m)) {
			throw new BadConfiguration("Method is already present in directory.");
		} else {
			String name = desc.name();
			if (_str2mem.containsKey(name)) {
				throw new BadConfiguration(name + " already exists in the directory as method "
						+ _str2mem.get(name).getName());
			} else {
				_str2mem.put(name, m);
				_mem2desc.put(m, desc);
			}
		}
	}



	public void add(Member m, String name) throws BadConfiguration {
		if (_str2mem.containsKey(name)) {
			throw new BadConfiguration(name + " already exists in the class as method"
					+ _str2mem.get(name).toString());
		} else if (_str2mem.containsKey(name)) {
			throw new BadConfiguration(name + " already exists in the class as member"
					+ _str2mem.get(name).toString());
		} else {
			_str2mem.put(name, m);
			_mem2desc.put(m, null);
		}
	}



	public void removeName(String name) throws BadConfiguration {
		if (_str2mem.containsKey(name)) {
			Member m = _str2mem.get(name);
			_str2mem.remove(name);
			_mem2desc.remove(m);
		} else {
			throw new BadConfiguration(name + " is not known.");
		}
	}

}
