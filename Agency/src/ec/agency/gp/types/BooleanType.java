package ec.agency.gp.types;

public class BooleanType implements DType{
	
	Boolean _value;
	
	public BooleanType(Boolean b){
		_value = b;
	}
	
	public BooleanType(Integer b){
		_value = (b == 0) ? false : true;
	}
	
	public BooleanType(Double b){
		_value = (b == 0.0) ? false : true;
	}
	
	public BooleanType(String b){
		_value = (b == "false" ) ? false : true;
	}

	@Override
	public Boolean asBoolean() {
		return _value;
	}

	@Override
	public Double asDouble() {
		return (_value) ? 1.0 : 0.0;
	}

	@Override
	public Integer asInteger() {
		return (_value) ? 1 : 0;
	}

	@Override
	public String asString() {
		return (_value) ? "true" : "false";
	}

	@Override
	public void fromBoolean(Boolean b) {
		_value = b;
	}

	@Override
	public void fromInteger(Integer i) {
		_value = (i == 0) ? false : true;
	}

	@Override
	public void fromDouble(Double d) {
		_value = (d == 0.0) ? false : true;
	}

	@Override
	public void fromString(String s) {
		_value = (s == "false") ? false : true;
	}

	@Override
	public void fromDType(DType t) {
		_value = t.asBoolean();
	}


}
