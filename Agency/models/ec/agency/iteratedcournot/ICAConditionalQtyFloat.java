package ec.agency.iteratedcournot;

import ec.vector.FloatVectorIndividual;

public class ICAConditionalQtyFloat extends FloatVectorIndividual implements
		IteratedCournotAgent {
	private static final long serialVersionUID = 1L;

	static final int posFirstQty = 0;
	static final int posCooperateTarget = 1;
	static final int posCooperateQty = 2;
	static final int posDefectQty = 3;
	
	@Override
	public float getProduction(ProductionStimulus prodStim) {
		if (prodStim.step == 0)
			return genome[posFirstQty];
		
		float othersQty = prodStim.othersLastProduction;
		if (othersQty > genome[posCooperateTarget])
			return genome[posDefectQty];
		else
			return genome[posCooperateQty];

	}

}
