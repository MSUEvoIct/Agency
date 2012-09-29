package abce.models.io.investment;

import ec.vector.FloatVectorIndividual;

public class SpitefulCooperator extends FloatVectorIndividual implements InvestmentFirm {
	private static final long serialVersionUID = 1L;

	@Override
	public double purchaseCapital(InvestmentModel sim, int marketNum) {
		return 10.0;
	}

	@Override
	public double setProduction(InvestmentModel sim, int marketNum) {
		double monopolyQty = sim.getMonopolyQty();
		if (sim.step == 0)
			return monopolyQty/3;
		
		double qtyLast = sim.totalQty(marketNum, sim.step-1);
		
		double deviation = Math.abs(qtyLast - monopolyQty);
		
		if (deviation < 5) {
			return monopolyQty / 3;
		} else {
			return monopolyQty / 1.5;
		}
	}

	@Override
	public boolean equals(Object ind) {
		if (this == ind)
			return true;
		else
			return false;
	}
	
	

}
