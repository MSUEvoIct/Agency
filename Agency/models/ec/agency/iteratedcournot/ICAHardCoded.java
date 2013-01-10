package ec.agency.iteratedcournot;

import ec.vector.FloatVectorIndividual;

public class ICAHardCoded extends FloatVectorIndividual implements IteratedCournotAgent {
	private static final long serialVersionUID = 1L;

	static final float firstQty = 25;
	static final float targetQty = 25;
	static final float targetTolerance = 1;
	static final float cooperateQty = 25;
	static final float defectQty = 35;
	
	@Override
	public float getProduction(ProductionStimulus prodStim) {
		if (prodStim.step == 0)
			return firstQty;
		
		float othersQty = prodStim.othersLastProduction;
		float deviance = Math.abs(targetQty-othersQty);
		if (deviance < targetTolerance)
			return cooperateQty;
		else
			return defectQty;
	
	}

}
