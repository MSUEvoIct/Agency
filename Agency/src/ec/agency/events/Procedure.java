package ec.agency.events;



import java.io.Serializable;

import ec.agency.util.BadConfiguration;



public interface Procedure extends Serializable {

	public void setup(EventProcedureArgs args) throws BadConfiguration;

	public void process(Object... context) throws Exception;

	public void finish();

}
