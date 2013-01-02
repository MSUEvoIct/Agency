package abce.agency.events;


import abce.util.events.EventProcedureDescription;
import abce.util.events.ValuedEventProcedure;
import abce.util.BadConfiguration;



public class ECJEventProcedure extends ValuedEventProcedure {

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
	public ECJEventProcedure(byte type, EventProcedureDescription desc) throws BadConfiguration {
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
