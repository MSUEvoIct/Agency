package abce.ecj;


import evoict.*;
import evoict.ep.*;



public class ECJValuedEventProcedure extends ValuedEventProcedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;



	/**
	 * Construct a new value event procedure for ECJ procedures
	 * 
	 * @param type
	 *            The event type
	 * @param desc
	 *            The event description describing the event
	 * @throws BadConfiguration
	 */
	public ECJValuedEventProcedure(byte type, EventProcedureDescription desc) throws BadConfiguration {
		super(type, desc);
	}



	@Override
	public void execute(Object... context) throws Exception {
		if (_action == null) {
			_action = getProcedureClass().newInstance();
			_action.setup(_arguments);
		}
		_action.process(context);
	}

}
