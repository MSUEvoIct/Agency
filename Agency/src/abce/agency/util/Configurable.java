package abce.agency.util;


public interface Configurable extends Settable {

	/**
	 * Get a Parameter with a particular name (null if not set)
	 * 
	 * @param name
	 * @return
	 */
	public Object get(String name);



	/**
	 * Set a key/value Parameter
	 * 
	 * @param name
	 * @param value
	 */
	@Override
	public void set(String name, Object value);



	/**
	 * Unset (remove) a parameter if it is present
	 * 
	 * @param name
	 */
	public void unset(String name);



	/**
	 * Returns true if a parameter with a particular name is set
	 * 
	 * @param name
	 * @return
	 */
	public Boolean isSet(String name);



	/**
	 * Helper function to return a Parameter with a particular name to an
	 * Integer (or null if not present)
	 * 
	 * @param name
	 * @return
	 */
	public Integer I(String name);



	/**
	 * Helper function to return a Parameter with a particular name to an Double
	 * (or null if not present)
	 * 
	 * @param name
	 * @return
	 */
	public Double D(String name);



	/**
	 * Helper function to return a Parameter with a particular name to an String
	 * (or null if not present)
	 * 
	 * @param name
	 * @return
	 */
	public String S(String name);



	/**
	 * Validation method to check whether or not a key exists with a particular
	 * type.
	 * 
	 * @param name
	 *            name of the key
	 * @param cl
	 *            class the key should be
	 * @return
	 *         true if the key exists and has a value of the type cl; otherwise
	 *         false
	 */
	public Boolean validate(String name, Class<?> cl);
}
