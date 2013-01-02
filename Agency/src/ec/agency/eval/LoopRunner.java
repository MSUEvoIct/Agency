package ec.agency.eval;

import java.util.Set;

import ec.EvolutionState;
import ec.Individual;
import ec.util.Parameter;

public class LoopRunner implements AgencyRunner {
	private static final long serialVersionUID = 1L;

	private EvolutionState evoState = null;
	private Parameter base = null;

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		this.evoState = evoState;
		this.base = base;
	}

	@Override
	public void runSimulations(GroupCreator gc, FitnessListener fl) {
		int simulationID = 0;
		
		while (gc.hasNext()) {
			Set<Individual> group = gc.next();
			AgencyModel sim = AgencyEvaluator.getSim(evoState, base);
			sim.addFitnessListener(fl);

			for (Individual ind : group) {
				sim.addIndividual(ind);
			}
			
			sim.setGeneration(evoState.generation);
			sim.setSimulationID(simulationID++);
			
			sim.run();

		}

	}



}
