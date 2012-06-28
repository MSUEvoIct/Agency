package abce.agency.util.graphs;


import abce.agency.util.Configurable;
import abce.agency.util.KeyValueStore;
import abce.agency.util.StringUtil;



/**
 * 
 * @author ruppmatt
 * 
 *         A Configurable is an extension of the Hierarchical class. It has
 *         configuration parameters associated with it.
 */

public class ConfigurableHNode extends HNode implements Configurable {

	private static final long	serialVersionUID	= 1L;
	protected KeyValueStore		_kv					= new KeyValueStore();



	/**
	 * Constuct a parentless Configurable
	 * 
	 * @param name
	 */
	public ConfigurableHNode(String name) {
		this(name, null);
	}



	/**
	 * Construct a Configurable with a particular parent
	 * 
	 * @param name
	 * @param parent
	 */
	public ConfigurableHNode(String name, HNode parent) {
		super(name, parent);
	}



	/**
	 * Returns the length of the largest key
	 * 
	 * @return
	 */
	protected int getMaxName() {
		int max = 0;
		for (String s : _kv.getParams().keySet())
			max = (s.length() > max) ? s.length() : max;
		return max;
	}



	/**
	 * Converts this object to a string.
	 */
	@Override
	public String toString(int level) {
		StringBuffer sb = new StringBuffer();

		// Print the type and name of the configurable
		StringUtil.repeat(sb, "   ", level);
		sb.append("[" + this.getClass().getSimpleName() + " " + getName() + "]" + ENDL);

		int max_name = getMaxName();

		// Indent and print each parameter
		for (String s : _kv.getParams().keySet()) {
			StringBuffer pstring = new StringBuffer();
			StringUtil.repeat(pstring, "   ", level + 1);
			pstring.append(s);
			int delta = max_name - s.length();
			StringUtil.repeat(pstring, " ", delta + 1);
			pstring.append("= " + get(s).toString());
			pstring.append(ENDL);
			sb.append(pstring);
		}
		for (HNode h : _children.values())
			sb.append(h.toString(level + 1));
		return sb.toString();
	}



	@Override
	public Object get(String name) {
		return _kv.get(name);
	}



	@Override
	public void set(String name, Object value) {
		_kv.set(name, value);
	}



	@Override
	public void unset(String name) {
		_kv.unset(name);
	}



	@Override
	public Boolean isSet(String name) {
		return _kv.isSet(name);
	}



	@Override
	public Integer I(String name) {
		return _kv.I(name);
	}



	@Override
	public Double D(String name) {
		return _kv.D(name);
	}



	@Override
	public String S(String name) {
		return _kv.S(name);
	}



	@Override
	public Boolean validate(String name, Class<?> cl) {
		return _kv.validate(name, cl);
	}

}
