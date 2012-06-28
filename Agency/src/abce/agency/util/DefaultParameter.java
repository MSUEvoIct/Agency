package abce.agency.util;


import java.io.Serializable;



/**
 * 
 * @author ruppmatt
 * 
 *         This class extends Paramter to allow for default values and
 *         descriptions.
 */
public class DefaultParameter extends Parameter implements Serializable {

	private static final long	serialVersionUID	= 1L;
	protected final Object		_defvalue;
	protected final String		_description;



	/**
	 * Construct a Parameter object with a default value and description
	 * 
	 * @param name
	 * @param value
	 * @param description
	 */
	DefaultParameter(String name, Object value, String description) {
		super(name, value);
		_defvalue = value;
		_description = description;
	}



	/**
	 * Return the default value
	 * 
	 * @return
	 */
	public Object defvalue() {
		return _defvalue;
	}



	/**
	 * Return the description
	 * 
	 * @return
	 */
	public String description() {
		return _description;
	}

}
