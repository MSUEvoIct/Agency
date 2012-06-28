package abce.agency.events;


import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;

import abce.agency.util.BadConfiguration;
import abce.agency.util.io.CommentStrippedInFile;



/**
 * The procedure manager maintains the set of EventProcedureContainers in a
 * dictionary keyed by the ID of the event. It also provides a centralized way
 * to create, add, and process events.
 * 
 * There is no process method (or abstract method) in this base container;
 * individual implementing managers should add their own process(...) method.
 * 
 * @author ruppmatt
 * 
 */
public abstract class EventProcedureManager implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;



	/**
	 * Construct a set of EventProcedureDescriptions from a file
	 * 
	 * @param path
	 *            String path to the file
	 * @return
	 *         A set of event procedure descriptions
	 * @throws BadConfiguration
	 * @throws FileNotFoundException
	 */
	public static EventProcedureDescription[] processFromFile(String path) throws BadConfiguration,
			FileNotFoundException {
		ArrayList<EventProcedureDescription> desc = new ArrayList<EventProcedureDescription>();
		CommentStrippedInFile in = new CommentStrippedInFile(path);
		while (in.hasNextLine()) {
			String line = in.nextLine();
			desc.add(processLine(line));
		}
		return desc.toArray(new EventProcedureDescription[desc.size()]);
	}



	/**
	 * Create an event procedure description from a single line containing the
	 * entire description
	 * 
	 * @param line
	 *            A string describing an event procedure
	 * @return
	 * @throws BadConfiguration
	 */
	public static EventProcedureDescription processLine(String line) throws BadConfiguration {
		return new EventProcedureDescription(line);
	}



	/**
	 * Add the event procedure to the manager
	 * 
	 * @param desc
	 *            An event procedure description
	 * @throws BadConfiguration
	 */
	public abstract void addEvent(EventProcedureDescription desc) throws BadConfiguration;



	/**
	 * Process the event with a particular event context and processing context
	 */
	public abstract void process(byte event_id, Object event_context, Object... execution_context);



	/**
	 * Notify all processes that it's time to end; process proceedures that
	 * should come at the end and release resources.
	 */
	public abstract void finish();
}
