package abce.models.io.iterated.cournot.investment;

import ec.vector.FloatVectorIndividual;

/**
 * SpitefulCooperator
 * 
 * If the quantity in the last period was within a certain threshold of the monopoly
 * quantity, SpitefulCooperator produces the cartel level of output.  If not, it
 * reverts to a static quantity specified in its genome.  The values of the genome
 * are used as follows:
 * 
 * genome[0]:  The default quantity if not cooperating as a cartel.
 * genome[1]:  The amount of capital to purchase each step.
 * ...[2]:  The accepted deviation from the cartel quantity for cooperation
 * 
 * @author kkoning
 *
 */
public class SpitefulCooperator extends FloatVectorIndividual implements InvestmentFirm {
	private static final long serialVersionUID = 1L;

	@Override
	public double purchaseCapital(InvestmentModel sim, int marketNum) {
		return genome[1];
	}

	@Override
	public double setProduction(InvestmentModel sim, int marketNum) {
		double monopolyQty = sim.getMonopolyQty();
		int numFirms = sim.qtyProduced.keySet().size();
		
		if (sim.step == 0)
			return monopolyQty / numFirms;
		
		double qtyLast = sim.totalQty(marketNum, sim.step-1);
		
		double deviation = Math.abs(qtyLast - monopolyQty);
		
		if (deviation < genome[2]) {
			return monopolyQty / numFirms;
		} else {
			return genome[0];
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
