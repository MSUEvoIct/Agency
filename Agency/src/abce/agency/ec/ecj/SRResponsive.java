package abce.agency.ec.ecj;


import java.lang.reflect.*;

import abce.agency.ec.*;
import ec.*;



/**
 * This interface allows for Response methods to be retrieved or set.
 * 
 * @author ruppmatt
 * 
 */
public interface SRResponsive {

	/**
	 * Retrieve the method that will be triggered via the SR.
	 * 
	 * @return
	 */
	public Method getResponse();



	/**
	 * Set the method that will be triggered via the SR.
	 * 
	 * @param m
	 */
	public void setResponse(EvolutionState state, StimulusResponse m);

}
