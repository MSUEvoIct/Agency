package ec.agency.events;


public interface EventProcedureContainer {

	/**
	 * Add an event action to the container
	 * 
	 * @param ea
	 */
	public void add(EventProcedure ea);



	/**
	 * Return event actions that should be triggered given the received context,
	 * removing them.
	 * 
	 * @param context
	 * @return
	 */
	public EventProcedure[] processContext(Object context);



	/**
	 * Return the event actions that should be triggered in the received context
	 * without removing them.
	 * 
	 * @param context
	 * @return
	 */
	public EventProcedure[] peekContext(Object context);



	/**
	 * Return the number of event actions meeting the received context
	 * 
	 * @param context
	 * @return
	 */
	public int numWithContext(Object context);



	/**
	 * Get all event action events.
	 * 
	 * @return
	 */
	public EventProcedure[] get();



	/**
	 * Return the size of the container
	 * 
	 * @return
	 */
	public int size();



	/**
	 * Finish all events; reclaim resources from procedures
	 */
	public void finish();

}
