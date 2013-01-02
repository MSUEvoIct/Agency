package ec.agency.events;

public class ContextFreeEventProcedure extends EventProcedure {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;



	public ContextFreeEventProcedure(byte id, EventProcedureDescription desc) {
		super(id, desc);
	}



	@Override
	public EventContext examine(Object context) {
		return EventContext.CURRENT;
	}



	@Override
	public void execute(Object... context) throws Exception {
		if (getProcedure() == null) {
			try {
				this._action = this._action_class.newInstance();
				this._action.setup(_arguments);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}
		getProcedure().process(context);
	}
}
