package abce.agency.ec.ecj;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ec.EvolutionState;
import ec.Individual;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class LoopRunner implements AgencyRunner {
	private static final long serialVersionUID = 1L;

	private MersenneTwisterFast random = null;
	private EvolutionState evoState = null;
	private Parameter base = null;
	private List<FitnessListener> fitnessListeners = new ArrayList<FitnessListener>();

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		this.evoState = evoState;
		this.base = base;

		int randSeed = evoState.parameters.getInt(base.push("seed"),
				new Parameter("seed").push("0"));

		random = new MersenneTwisterFast(randSeed);

	}

	@Override
	public void runSimulations(GroupCreator gc, FitnessListener fl) {
		int simulationID = 0;
		
		while (gc.hasNext()) {
			Set<Individual> group = gc.nextGroup();
			AgencyECJSimulation sim = getSim();
			sim.addFitnessListener(fl);

			for (Individual ind : group) {
				sim.addIndividual(ind);
			}
			
			sim.setGeneration(evoState.generation);
			sim.setSimulationID(simulationID++);
			
			sim.run();

		}

	}

	private AgencyECJSimulation getSim() {
		Class groupCreatorClass = (Class) evoState.parameters
				.getClassForParameter(base.push("sim"), null, AgencyECJSimulation.class);
		AgencyECJSimulation sim = null;

		try {
			sim = (AgencyECJSimulation) groupCreatorClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		sim.setSeed(this.random.nextInt());
		sim.setup(evoState, base.push("sim"));

		return sim;
	}

}
