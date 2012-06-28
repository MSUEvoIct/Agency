package abce.agency.ec.ecj.types;

public interface DType {
	
	public Boolean asBoolean();
	public Double  asDouble();
	public Integer asInteger();
	public String  asString();
	
	public void fromDType(DType t);
	
	public void fromBoolean(Boolean b);
	public void fromInteger(Integer i);
	public void fromDouble(Double d);
	public void fromString(String s);

}
