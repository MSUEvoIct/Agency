package abce.models.iteratedpd;

import ec.vector.BitVectorIndividual;
import abce.agency.firm.Firm;
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * Agent that plays the iterated prisoners dillemea game according to a GA.
 * Inspired by work of Axelrod (1984).
 *
 * Bits 0-63 determine strategy at step 4 and afterward, with the following pattern
 * 0x1 - Other agent defected t-1
 * 0x2 - Other agent defected t-2
 * 0x4 - Other agent defected t-3
 * 0x8 - This agent defected t-1
 * 0x10 - This agent defected t-2
 * 0x20 - This agent defected t-3
 * 
 * Bits 64-79 determine strategy at step 3.  0x40, +
 * 0x1 - Other agent defected t-1
 * 0x2 - Other agent defected t-2
 * 0x4 - This agent defected t-1
 * 0x8 - This agent defected t-2
 * 
 * Bits 80-84 determine strategy at step 2.  0x50 +
 * 0x1 - Other agent defected t-1
 * 0x2 - This agent defected t-1
 * 
 * Bit 85, determine strategy at step 1. 0x54 
 *  
 * 
 * @author kkoning
 * 
 */
public class IteratedPDAgentGA extends Firm implements IteratedPDAgent {

	public double totalRevenue = 0.0;
	public BitVectorIndividual ind;
	
	public IteratedPDAgentGA(BitVectorIndividual ind) {
		this.ind = ind;
	}
	
	
	@Override
	public void step(SimState state) {
		super.step(state);
		
		// determine whether or not to defect.
		IteratedPDSimulation sim = (IteratedPDSimulation) state;
		
		boolean defections[] = sim.defections.get(this);
		
		Boolean toDefect = null;
		
		
		// if step 1, just take our decision directly from the genome.
		if (this.numSteps() == 0) {
			toDefect = ind.genome[0x54];
		}
		
		if (this.numSteps() == 1) {
			int position = 0x50;
			if (sim.getOtherAgent(this).defected(state, 1))
				position += 0x1;
			if (this.defected(state, 1))
				position += 0x2;
			toDefect = ind.genome[position];
		}
		
		if (this.numSteps() == 2) {
			int position = 0x40;
			if (sim.getOtherAgent(this).defected(state, 1))
				position += 0x1;
			if (sim.getOtherAgent(this).defected(state, 2))
				position += 0x2;
			if (this.defected(state, 1))
				position += 0x4;
			if (this.defected(state, 2))
				position += 0x8;
			toDefect = ind.genome[position];
		}
		
		if (this.numSteps() >= 3) {
			int position = 0x0;
			
			if (sim.getOtherAgent(this).defected(state, 1))
				position += 0x1;
			if (sim.getOtherAgent(this).defected(state, 2))
				position += 0x2;
			if (sim.getOtherAgent(this).defected(state, 3))
				position += 0x4;
			if (this.defected(state, 1))
				position += 0x8;
			if (this.defected(state, 2))
				position += 0x10;
			if (this.defected(state, 3))
				position += 0x20;
			toDefect = ind.genome[position];
		}

		if (toDefect == null)
			throw new RuntimeException("Should have made the decision on whether or not to defect by now");
		
		// actualize our decision
		defections[shortIndex()] = toDefect;
		
	}

	@Override
	public boolean defected(SimState state, int stepsAgo) {
		IteratedPDSimulation sim = (IteratedPDSimulation) state;
		boolean[] defections = sim.defections.get(this);
		return defections[shortIndex(stepsAgo)];
	}


	@Override
	public void earn(double amount) {
		this.getAccounts().revenue(amount);
	}


	@Override
	public double getTotalRevenue() {
		return this.getAccounts().getTotalRevenue();
	}
	
}
