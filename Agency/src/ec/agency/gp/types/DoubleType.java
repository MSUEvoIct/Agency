package ec.agency.gp.types;

public class DoubleType implements DType{

	protected Double _value;
	
	public DoubleType(Double v){
		_value = v;
	}
	
	public DoubleType(Integer v){
		_value = v * 1.0;
	}
	
	public DoubleType(String v){
		_value = Double.valueOf(v);
	}
	
	public DoubleType(Boolean v){
		_value = (v) ? 1.0 : 0.0;
	}
	
	
	@Override
	public Boolean asBoolean() {
		return (_value == 0) ? false : true;
	}

	@Override
	public Double asDouble() {
		return _value;
	}

	@Override
	public Integer asInteger() {
		return _value.intValue();
	}
	
	public String asString(){
		return _value.toString();
	}

	@Override
	public void fromBoolean(Boolean b) {
		_value = (b) ? 1.0 : 0.0;
		
	}

	@Override
	public void fromInteger(Integer i) {
		_value = i.doubleValue();		
	}

	@Override
	public void fromDouble(Double d) {
		_value = d;
	}

	@Override
	public void fromString(String s) {
		_value = Double.valueOf(s);
	}

	@Override
	public void fromDType(DType t) {
		_value = t.asDouble();
	}
	

}
