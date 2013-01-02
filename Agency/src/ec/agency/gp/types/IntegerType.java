package ec.agency.gp.types;

public class IntegerType implements DType{

	protected Integer _value; 
	
	public IntegerType(Boolean i){
		_value = (i) ? 1 : 0;
	}
	
	public IntegerType(Integer i){
		_value = i;
	}
	
	public IntegerType(Double i){
		_value = i.intValue();
	}
	
	public IntegerType(String i){
		_value = Integer.valueOf(i);
	}
	
	
	
	@Override
	public Boolean asBoolean() {
		return (_value == 0) ? false : true;
	}

	@Override
	public Double asDouble() {
		return _value.doubleValue();
	}

	@Override
	public Integer asInteger() {
		return _value;
	}
	
	public String asString(){
		return _value.toString();
	}

	@Override
	public void fromBoolean(Boolean b) {
		_value = (b) ? 1 : 0;
		
	}

	@Override
	public void fromInteger(Integer i) {
		_value = i;
	}

	@Override
	public void fromDouble(Double d) {
		_value = d.intValue();
	}

	@Override
	public void fromString(String s) {
		_value = Integer.valueOf(s);
	}

	@Override
	public void fromDType(DType t) {
		_value = t.asInteger();
	}

	
}
