package ec.agency;

/**
 * Reactive agents act in their environments by reacting to stimuli objects.
 * These objects describe a situation or piece of information based on which the
 * agent may want to take an action. The system does not automatically create
 * these stimuli, they will be specific to a problem domain and should be
 * emitted by code written by the domain modeler.
 * 
 * Agency does provide an evolutionary computation system designed to handle
 * these stimuli objects.  Alternatively, domain modelers may which to implement
 * an explicit decision process by writing code for this function directly.
 * 
 * @author kkoning
 * 
 */
public interface ReactiveAgent {
	/**
	 * React to the specified stimulus; the agent is responsible for, based on
	 * the stimulus, deciding whether to take any action(s) and executing those
	 * actions.
	 * 
	 * @param sr
	 */
	public void reactTo(StimulusResponse sr);
}
