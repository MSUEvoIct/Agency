package abce.io.models.simternet;

import ec.EvolutionState;
import ec.util.ParameterDatabase;
import sim.engine.SimState;

public class Simternet extends SimState {
	private static final long serialVersionUID = 1L;
	
	public static final String configPrefix = "simternet";
	
	/**
	 * Contains a reference back to the ECJ Evolution
	 */
	public ParameterDatabase _parameterDatabase;
	
	
	public Simternet(ParameterDatabase pd, long seed) {
		super(seed);
		_parameterDatabase = pd;
		
	}

	
	

}
