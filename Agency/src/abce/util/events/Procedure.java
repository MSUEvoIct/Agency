package abce.util.events;



import java.io.Serializable;

import abce.util.BadConfiguration;


public interface Procedure extends Serializable {

	public void setup(EventProcedureArgs args) throws BadConfiguration;

	public void process(Object... context) throws Exception;

	public void finish();

}
