package ec.agency.eval;

import java.util.Set;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class LoopRunner implements AgencyRunner {
	private static final long serialVersionUID = 1L;

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		// Nothing to do
	}

	@Override
	public void runModel(Runnable model) {
		// Simply run the model directly
		model.run();
	}

	@Override
	public void finish() {
		// Nothing to do; no asynchronous model execution
	}



}
