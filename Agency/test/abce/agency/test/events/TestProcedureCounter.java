package abce.agency.test.events;


import abce.agency.events.EventProcedureArgs;
import abce.agency.events.Procedure;



public class TestProcedureCounter implements Procedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	public int					counter;



	@Override
	public void setup(EventProcedureArgs args) {
		counter = 0;
	}



	@Override
	public void process(Object... context) {
		counter++;
	}



	@Override
	public void finish() {
	}

}
