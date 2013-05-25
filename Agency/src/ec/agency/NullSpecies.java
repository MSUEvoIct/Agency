package ec.agency;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.Species;
import ec.util.Parameter;

public class NullSpecies extends Species {
	private static final long serialVersionUID = 1L;

	Parameter base = new Parameter("nullspecies");
	
	@Override
	public Parameter defaultBase() {
		return base;
	}

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state,base);
//		// Just taking some code from ec.Species because only part of it
//		// is unnecessary.
//		
//		// load our individual prototype
//		i_prototype = (Individual) (state.parameters.getInstanceForParameter(
//				base.push(P_INDIVIDUAL), null, Individual.class));
//		// set the species to me before setting up the individual, so they know
//		// who I am
//		i_prototype.species = this;
//		i_prototype.setup(state, base.push(P_INDIVIDUAL));
//		
//		f_prototype = (Fitness) state.parameters.getInstanceForParameter(
//	            base.push(P_FITNESS),null,
//	            Fitness.class);
//	        f_prototype.setup(state,base.push(P_FITNESS));
	}

}
