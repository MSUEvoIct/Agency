package abce.agency.ec.ecj;

import ec.EvolutionState;
import ec.util.Parameter;

public class SimulationInitializer implements ec.Setup {
	private static final long serialVersionUID = 1L;

	private EvolutionState evoState;
	private Parameter base;
	private Class<? extends AgencyModel> simClass = null;

	@SuppressWarnings("unchecked")
	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		this.evoState = evoState;
		this.base = base;

		simClass = (Class<? extends AgencyModel>) evoState.parameters
				.getClassForParameter(base, null, AgencyModel.class);

	}

	public AgencyModel getSimulation() {
		AgencyModel aes = null;
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
