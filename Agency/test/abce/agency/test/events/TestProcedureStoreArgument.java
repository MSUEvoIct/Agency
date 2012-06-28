package abce.agency.test.events;


import abce.agency.events.EventProcedureArgs;
import abce.agency.events.Procedure;



/**
 * This test procedure stores an argument received via the setup method in the
 * ProcedureTestContext that gets sent to the procedure in the process method.
 */
public class TestProcedureStoreArgument implements Procedure {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;

	protected EventProcedureArgs	args;



	@Override
	public void setup(EventProcedureArgs args) {
		this.args = args;
	}



	@Override
	public void process(Object... context) {
		ProcedureTestContext ptc = (ProcedureTestContext) context[0];
		ptc.received_argument = (args.containsKey("to_store")) ? args.get("to_store") : "missing";
	}



	@Override
	public void finish() {
	}

}
