package abce.util.events;


import java.io.Serializable;



/**
 * An EventProceedure contains information about an event and the associated
 * procedure that should be executed upon that event.
 * 
 * @author ruppmatt
 * 
 */
public abstract class EventProcedure implements Serializable {

	private static final long				serialVersionUID	= 1L;
	protected byte							_event_id;
	protected Class<? extends Procedure>	_action_class		= null;
	protected Procedure						_action				= null;
	protected boolean						_completed			= false;
	protected EventProcedureArgs			_arguments			= null;



	public EventProcedure(byte id, EventProcedureDescription desc) {
		_event_id = id;
		_action_class = desc.getProcedureClass();
		_arguments = desc.getProcedureArguments();
	}



	/**
	 * Get the type of the event
	 * 
	 * @return
	 */
	public byte type() {
		return _event_id;
	}



	/**
	 * Return the EventContext with regard to the object received
	 * 
	 * @param context
	 *            Information to decided whether the event did/is/will happen
	 * @return
	 */
	public abstract EventContext examine(Object context);



	/**
	 * Execute the event procedure.
	 * 
	 * @param context
	 *            Implementation-specific objects needed for procedure
	 *            execution. Construction of the procedure object may also take
	 *            place if needed in this method.
	 */
	public abstract void execute(Object... context) throws Exception;



	/**
	 * Return the procedure object associated with this event. This can be null
	 * if it hasn't been created (e.g. by execute(...)) yet.
	 * 
	 * @return
	 *         Procedure associated with this event
	 */
	public Procedure getProcedure() {
		return _action;
	}



	/**
	 * Return the class of the procedure associated with this event
	 * 
	 * @return
	 *         The class of the procedure associated with this event.
	 */
	public Class<? extends Procedure> getProcedureClass() {
		return _action_class != null ? _action_class : Procedure.class;
	}



	/**
	 * The dictionary (string key to string value) argument pairs associated
	 * with this class
	 * 
	 * @return
	 */
	public EventProcedureArgs getArguments() {
		return _arguments;
	}



	/**
	 * Returns whether this event and associated procedure are finished
	 * 
	 * @return
	 *         True if procedure will never execute again for this event
	 */
	public boolean finished() {
		return _completed;
	}



	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("EventProcedure: " + _event_id + " ~ " + getProcedureClass() + " " + getArguments());
		return buf.toString();
	}
}
