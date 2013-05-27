package ec.agency.prisonersdilemma;

import ec.agency.NullIndividual;

public class TitForTatPrisoner extends NullIndividual implements Prisoner {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean defect(InterrogationStimulus is) {
		if (is.step == 0)
			return false;
		
		return is.opponentsPlays.get(is.step-1);
	}

}
