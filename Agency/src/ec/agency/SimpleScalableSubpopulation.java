package ec.agency;

import ec.EvolutionState;
import ec.Group;
import ec.Individual;
import ec.Subpopulation;
import ec.util.Parameter;

public class SimpleScalableSubpopulation extends Subpopulation implements
		ScalableSubpopulation {
	private static final long serialVersionUID = 1L;

	private static final String P_minIndividuals = "minIndividuals";
	private static final String P_subPopulationGroup = "subpopGroup";

	int minIndividuals;
	int subPopulationGroup;
	int targetSize;

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		minIndividuals = state.parameters.getInt(base.push(P_minIndividuals),
				null);
		subPopulationGroup = state.parameters.getInt(
				base.push(P_subPopulationGroup), null);
		targetSize = this.individuals.length;

	}

	@Override
	public int getSubpopulationGroup() {
		return subPopulationGroup;
	}

	@Override
	public void setTargetSize(int numIndividuals) {
		if (numIndividuals < minIndividuals)
			targetSize = minIndividuals;
		else
			targetSize = numIndividuals;

	}
	
	@Override
	public int getMinSize() {
		return minIndividuals;
	}

	@Override
	public Group emptyClone() {
		// Taken from ECJ's ec.Subpopulation, except p.individuals
		// is initialized to targetSize
		
		try {
			Subpopulation p = (Subpopulation) clone();
			p.species = species; // don't throw it away...maybe this is a bad
									// idea...
			p.individuals = new Individual[targetSize]; // empty
			return p;
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}

	}

}
