package abce.util.events;


import java.io.Serializable;
import abce.util.BadConfiguration;



/**
 * EventProcedureDescription stores and processes string-based information about
 * the events and triggered procedures. An event is stored in the form:
 * 
 * EventType [value] ~ ActionName [arg0=value, arg1=value, ..., argN=value]
 * 
 * This class splits the event into the individual components, checks the
 * validity of the components, and maps ActionName into the procedure
 * class it refers to. BadConfiguration exceptions are thrown if there is
 * improper formatting or the target procedure class is not found.
 * 
 * @author ruppmatt
 * 
 */
public class EventProcedureDescription implements Serializable {

	private static final long	serialVersionUID	= 1L;
	String						_event_type = null;
	String						_event_value = null;
	Class<? extends Procedure>	_action_class = null;
	EventProcedureArgs			_arguments			= new EventProcedureArgs();



	/**
	 * Split the line into its EventAction components
	 * 
	 * @param line
	 *            in the form EventType [value] ~ ActionName [arg0=value,
	 *            arg1=value, ..., argN=value]
	 * @throws BadConfiguration
	 */
	public EventProcedureDescription(String line) throws BadConfiguration {
		buildFromLine(line);
	}



	/**
	 * Builds a description with pre-parsed information
	 * 
	 * @param event_type
	 * @param value
	 * @param action_name
	 * @param args
	 * @throws BadConfiguration
	 */
	public EventProcedureDescription(String event_type, String value, String procedure_name, String[] args)
			throws BadConfiguration {
		_event_type = event_type;
		_event_value = value;
		_action_class = getProcedure(procedure_name);
		_arguments = getArguments(args);
	}



	/**
	 * Construct a class from already constructed members
	 * 
	 * @param event_type
	 * @param event_value
	 * @param proc
	 * @param args
	 */
	public EventProcedureDescription(String event_type, String event_value, Class<? extends Procedure> proc,
			EventProcedureArgs args) {
		_event_type = event_type;
		_event_value = event_value;
		_action_class = proc;
		_arguments = args;
	}



	/**
	 * Split a line into its EventAction components
	 * 
	 * @param line
	 *            line to parse
	 * @param event
	 *            returns event string
	 * @param value
	 *            returns value string
	 * @param action
	 *            returns action string
	 * @param args
	 *            returns array of args
	 * @throws BadConfiguration
	 */
	protected void buildFromLine(String line)
			throws BadConfiguration {
		String[] parts = line.split("~");

		if (parts.length != 2) {
			throw new BadConfiguration("Event line is not in the form EVENT ~ PROCEDURE");
		}
		String[] event_tok = parts[0].split("\\s+", 2);
		if (event_tok.length < 1) {
			throw new BadConfiguration("Event part of Event~Procedre line is not formatted correctly.");
		}
		_event_type = event_tok[0].trim();
		_event_value = (event_tok.length > 1) ? event_tok[1].trim() : null;

		String[] action_tok = parts[1].trim().split(" ", 2);
		if (action_tok.length < 1) {
			throw new BadConfiguration("Procedure part of Event~Procedure line is not formatted correctly.");
		}
		_action_class = getProcedure(action_tok[0]);
		_arguments = (action_tok.length > 1) ? getArguments(action_tok[1].split(",")) : new EventProcedureArgs();

	}



	/**
	 * Break array of arguments into key/value pairs stored in
	 * EventActionArguments
	 * 
	 * @param args
	 *            Array of key value pairs in the form: key=value
	 * @return
	 * @throws BadConfiguration
	 *             If strings are improperly formatted
	 */
	protected EventProcedureArgs getArguments(String[] args) throws BadConfiguration {
		EventProcedureArgs retval = new EventProcedureArgs();
		for (String s : args) {
			String[] kv = s.split("=");
			if (kv.length != 2)
				throw new BadConfiguration("Argument not formatted properly for " + _action_class.getCanonicalName());
			else {
				retval.put(kv[0].trim(), kv[1].trim());
			}
		}
		return retval;
	}



	/**
	 * Returns the class associated with the named action event protoype
	 * 
	 * @param name
	 *            canonical name of action
	 * @throws BadConfiguration
	 */
	@SuppressWarnings("unchecked")
	protected Class<? extends Procedure> getProcedure(String name) throws BadConfiguration {
		Class<?> c = null;
		if (name == null) {
			throw new BadConfiguration("Cannot find a proceedure with a null name.");
		}
		try {
			c = Class.forName(name);
			if (!Procedure.class.isAssignableFrom(c))
				throw new BadConfiguration("Unable to find valid class: " + name);
		} catch (ClassNotFoundException e) {
			throw new BadConfiguration("Unable to find class: " + name);
		}
		return (Class<? extends Procedure>) c;
	}



	/**
	 * Return the string type. In the statement
	 * "GENERATION 0:10:end ~ SomeProcedure", this method would return
	 * "GENERATION"
	 * 
	 * @return
	 */
	public String getEventType() {
		return _event_type;
	}



	/**
	 * Get the value of the type; may be null if the event type doesn't need a
	 * value. This may also contain information about nested events. For
	 * example, if the entire EVENT portion of the EVENT~PROCEDURE statement
	 * reads: GENERATION 0:10:end STEP 0:10:end ~ SomeProcedure, this method
	 * will return "0:10:end STEP 0:10:END". Consequently, EventProcedures that
	 * need to base their state off of getEventValue() should parse this string
	 * as if it contains additional tokens.
	 * 
	 * @return
	 */
	public String getEventValue() {
		return _event_value;
	}



	/**
	 * Gets the Proceedure class triggered by this event.
	 * 
	 * @return
	 */
	public Class<? extends Procedure> getProcedureClass() {
		return _action_class;
	}



	/**
	 * Gets the key/value arguments for the Procedure triggered by this event
	 * 
	 * @return
	 */
	public EventProcedureArgs getProcedureArguments() {
		return _arguments;
	}



	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		String class_name = _action_class == null ? "null" : _action_class.toString();
		buf.append("Event<" + String.valueOf(_event_type) + "," + _event_value + "," + class_name + ","
				+ _arguments.toString() + ">");
		return buf.toString();
	}
}
