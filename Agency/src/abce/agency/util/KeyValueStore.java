package abce.agency.util;


import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;



public class KeyValueStore implements Configurable, Serializable, Cloneable {

	private static final long				serialVersionUID	= 1L;
	protected LinkedHashMap<String, Object>	_lhm_params			= new LinkedHashMap<String, Object>();
	protected Map<String, Object>			_params				= Collections.synchronizedMap(_lhm_params);



	public KeyValueStore() {
	}



	public LinkedHashMap<String, Object> getParams() {
		return _lhm_params;
	}



	@Override
	public Object get(String name) {
		if (isSet(name))
			return _params.get(name);
		else
			return null;
	}



	@Override
	public void set(String name, Object value) {
		_params.put(name, value);
	}



	@Override
	public void unset(String name) {
		if (isSet(name)) {
			_params.remove(name);
		}
	}



	@Override
	public Boolean isSet(String name) {
		return _params.containsKey(name);
	}



	@Override
	public Integer I(String name) {
		return _params.containsKey(name) ? (Integer) _params.get(name) : null;
	}



	@Override
	public Double D(String name) {
		return _params.containsKey(name) ? (Double) _params.get(name) : null;
	}



	@Override
	public String S(String name) {
		return _params.containsKey(name) ? _params.get(name).toString() : null;
	}



	public Boolean validate(String name, Class<?> cl) {
		if (isSet(name) && get(name) != null && cl.isAssignableFrom(get(name).getClass())) {
			return true;
		} else {
			return false;
		}
	}



	@Override
	@SuppressWarnings("unchecked")
	public Object clone() {
		KeyValueStore newkv = new KeyValueStore();
		newkv._lhm_params = (LinkedHashMap<String, Object>) _lhm_params.clone();
		newkv._params = Collections.synchronizedMap(_lhm_params);
		return newkv;
	}

}
