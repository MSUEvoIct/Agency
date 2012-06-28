package abce.agency.util.graphs;


import abce.agency.util.Configurable;
import abce.agency.util.KeyValueStore;



public class ConfigurableDANode extends DANode implements Configurable {

	private static final long	serialVersionUID	= 1L;
	protected KeyValueStore		_kv					= new KeyValueStore();



	public ConfigurableDANode(DAGraph graph) {
		super(graph);
	}



	public ConfigurableDANode(String name, DAGraph graph) {
		super(name, graph);
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
