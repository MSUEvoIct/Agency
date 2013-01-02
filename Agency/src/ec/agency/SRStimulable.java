package ec.agency;


import ec.EvolutionState;
import ec.agency.reflection.RestrictedMethodDictionary;



/**
 * This interface allows for Stimulus paths to be retrieved or set.
 * 
 * @author ruppmatt
 * 
 */
public interface SRStimulable {

	/**
	 * Get the stimulus path used by this object.
	 * 
	 * @return
	 *         a stimulus path String
	 */
	public String getStimulusPath();



	/**
	 * Set the stimulus path for this object
	 * 
	 * @param state
	 *            EvolutionState instance
	 * @param thread
	 *            thread in EvolutionState making the request
	 * @param dict
	 *            A dictionary of allowed values
	 */
	public void setStimulusPath(EvolutionState state, int thread, RestrictedMethodDictionary dict);
}
