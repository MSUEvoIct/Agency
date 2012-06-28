package abce.agency.util;


import java.io.Serializable;



/**
 * 
 * @author ruppmatt
 * 
 *         Generic Parameter class to hold key/value pairs.
 */

public class Parameter implements Serializable {

	private static final long	serialVersionUID	= 1L;
	protected final String		_key;
	Object						_value;



	/**
	 * Construct a Parameter object with a particular key and value
	 * 
	 * @param key
	 * @param value
	 */
	public Parameter(String key, Object value) {
		_key = key;
		_value = value;
	}



	/*
	 * Return the value of the Parameter
	 */
	public Object value() {
		return _value;
	}



	/*
	 * Return the key of the Parameter
	 */
	public String key() {
		return _key;
	}



	/**
	 * Set the value of the Parameter
	 * 
	 * @param value
	 */
	public void set(Object value) {
		_value = value;
	}

}
