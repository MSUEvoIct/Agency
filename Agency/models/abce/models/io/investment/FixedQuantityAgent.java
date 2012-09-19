package abce.models.io.investment;

import ec.vector.FloatVectorIndividual;

public class FixedQuantityAgent extends FloatVectorIndividual implements InvestmentFirm {
	private static final long serialVersionUID = 1L;

	@Override
	public double purchaseCapital(InvestmentModel sim, int marketNum) {
		return genome[0];
	}

	@Override
	public double setProduction(InvestmentModel sim, int marketNum) {
		return genome[1];
	}

	@Override
	public boolean equals(Object ind) {
		if (this == ind)
			return true;
		else
			return false;
	}
	
	

}
