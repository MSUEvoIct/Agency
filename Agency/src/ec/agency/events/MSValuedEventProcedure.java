package ec.agency.events;


import ec.agency.util.BadConfiguration;



public class MSValuedEventProcedure extends ValuedEventProcedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;



	public MSValuedEventProcedure(byte type, EventProcedureDescription desc) throws BadConfiguration {
		super(type, desc);
	}



	@Override
	public void execute(Object... context) throws Exception {
		if (_action == null) {
			_action = _action_class.newInstance();
			_action.setup(_arguments);
		}
		_action.process(context);
	}

}
