package ec.agency.gp.types;

public class StringType implements DType {

	String _value;
	
	public StringType(Boolean s){
		_value = (s) ? "true" : "false";
	}
	
	public StringType(Integer s){
		_value = Integer.toString(s);
	}
	
	public StringType(Double s){
		_value = Double.toString(s);
	}
	
	public StringType(String s){
		_value = s;
	}

	@Override
	public Boolean asBoolean() {
		return (_value == "true") ? true : false;
	}

	@Override
	public Double asDouble() {
		return Double.valueOf(_value);
	}

	@Override
	public Integer asInteger() {
		return Integer.valueOf(_value);
	}

	@Override
	public String asString() {
		return _value;
	}

	@Override
	public void fromBoolean(Boolean b) {
		_value = (b) ? "true" : "false";
	}

	@Override
	public void fromInteger(Integer i) {
		_value = Integer.toString(i);
	}

	@Override
	public void fromDouble(Double d) {
		_value = Double.toString(d);
	}

	@Override
	public void fromString(String s) {
		_value = s;
	}

	@Override
	public void fromDType(DType t) {
		_value = t.asString();
	}
	
	
}
