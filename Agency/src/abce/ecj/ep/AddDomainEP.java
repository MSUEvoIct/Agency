package abce.ecj.ep;


import abce.ecj.EPSimpleEvolutionState;
import abce.util.events.EventProcedureArgs;
import abce.util.events.EventProcedureDescription;
import abce.util.events.Procedure;
import abce.util.BadConfiguration;



/**
 * This procedure only works if the ECJ evolution state is an
 * EASimpleEvolutionState.
 * 
 * Procedure Arguments:
 * 
 * desc
 * The a valid Evolution Procedure Description in the form
 * event_string [value] ~ procedure_class [arg1=val1, ..., argN =valN]
 * 
 * @author ruppmatt
 * 
 */
public class AddDomainEP implements Procedure {

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
		EPSimpleEvolutionState state = (EPSimpleEvolutionState) context[0];
		if (args.containsKey("desc")) {
			try {
				state.domain_events.add(new EventProcedureDescription(args.get("desc")));
			} catch (BadConfiguration e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}



	@Override
	public void finish() {
	}

}
