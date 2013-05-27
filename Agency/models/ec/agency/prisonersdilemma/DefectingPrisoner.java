package ec.agency.prisonersdilemma;

import ec.agency.NullIndividual;

public class DefectingPrisoner extends NullIndividual implements Prisoner {

	@Override
	public boolean defect(InterrogationStimulus is) {
		return true;
	}

}
