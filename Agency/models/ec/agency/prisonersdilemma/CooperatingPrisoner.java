package ec.agency.prisonersdilemma;

import ec.agency.NullIndividual;

public class CooperatingPrisoner extends NullIndividual implements Prisoner {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean defect(InterrogationStimulus is) {
		return false;
	}

}
