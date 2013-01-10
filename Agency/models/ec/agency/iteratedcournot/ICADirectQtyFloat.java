package ec.agency.iteratedcournot;

import ec.vector.FloatVectorIndividual;

public class ICADirectQtyFloat extends FloatVectorIndividual implements IteratedCournotAgent {
	private static final long serialVersionUID = 1L;

	@Override
	public float getProduction(ProductionStimulus prodStim) {
		// let the genome set the production directly; use the first position
		return genome[0];
	}

	
	
}
