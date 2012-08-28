package abce.agency.ec.ecj;

import ec.EvolutionState;
import ec.util.Parameter;

public class SimulationInitializer implements ec.Setup {
	private static final long serialVersionUID = 1L;

	private EvolutionState evoState;
	private Parameter base;
	private Class<? extends AgencyECJSimulation> simClass = null;

	@SuppressWarnings("unchecked")
	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		this.evoState = evoState;
		this.base = base;

		simClass = (Class<? extends AgencyECJSimulation>) evoState.parameters
				.getClassForParameter(base, null, AgencyECJSimulation.class);

	}

	public AgencyECJSimulation getSimulation() {
		AgencyECJSimulation aes = null;
		try {
			aes = simClass.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		aes.setup(evoState, base);

		return aes;
	}

}
