package ec.agency.iteratedcournot;

import ec.vector.BitVectorIndividual;

public class ICADirectBitArray extends BitVectorIndividual implements IteratedCournotAgent {
	private static final long serialVersionUID = 1L;

	@Override
	public float getProduction(ProductionStimulus prodStim) {
		int production = 0;
		for (int i = 0; i < genome.length; i++) {
			int toAdd = (int) Math.pow(2, i);
			if(genome[i])
				production += toAdd;
		}
		return (float) production;
	}

}
