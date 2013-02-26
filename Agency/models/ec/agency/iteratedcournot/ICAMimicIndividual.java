package ec.agency.iteratedcournot;

import ec.vector.FloatVectorIndividual;

/**
 * First step production is set by the first position of the genome. In all
 * subsequent steps, produce exactly the same amount as the competitor does in
 * the previous step.
 * 
 * @author kkoning
 * 
 */
public class ICAMimicIndividual extends FloatVectorIndividual implements
		IteratedCournotAgent {
	private static final long serialVersionUID = 1L;

	@Override
	public float getProduction(ProductionStimulus prodStim) {
		if (prodStim.step == 0)
			return genome[0];
		else
			return prodStim.othersLastProduction;
	}

}
