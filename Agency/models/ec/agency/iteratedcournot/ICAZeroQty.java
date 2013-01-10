package ec.agency.iteratedcournot;

import ec.vector.FloatVectorIndividual;

/**
 * Always produces Qty=0.  For debugging purposes.s
 * 
 * @author kkoning
 *
 */
public class ICAZeroQty extends FloatVectorIndividual implements IteratedCournotAgent {
	private static final long serialVersionUID = 1L;

	@Override
	public float getProduction(ProductionStimulus prodStim) {
		return 0;
	}

}
