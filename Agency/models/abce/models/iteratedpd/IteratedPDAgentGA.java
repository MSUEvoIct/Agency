package abce.models.iteratedpd;

import ec.vector.BitVectorIndividual;

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
public class IteratedPDAgentGA implements IteratedPDAgent {

	public double earnings;
	public BitVectorIndividual ind;
	
	public IteratedPDAgentGA(BitVectorIndividual ind) {
		this.ind = ind;
	}
	
	
	public boolean defect(IteratedPDSimulation state) {
		
		Boolean toDefect = null;
		boolean[] myDefections = state.getDefections(this);
		boolean[] opponentDefections = state.getDefections(state.getOtherAgent(this));
		
		// if step 1, just take our decision directly from the genome.
		if (state.step == 0) {
			toDefect = ind.genome[0x54];
		}
		
		if (state.step == 1) {
			int position = 0x50;
			if (opponentDefections[0])
				position += 0x1;
			if (myDefections[0])
				position += 0x2;
			toDefect = ind.genome[position];
		}
		
		if (state.step == 2) {
			int position = 0x40;
			if (opponentDefections[1])
				position += 0x1;
			if (opponentDefections[0])
				position += 0x2;
			if (myDefections[1])
				position += 0x4;
			if (myDefections[0])
				position += 0x8;
			toDefect = ind.genome[position];
		}
		
		if (state.step >= 3) {
			int position = 0x0;
			
			if (opponentDefections[state.step - 1])
				position += 0x1;
			if (opponentDefections[state.step - 2])
				position += 0x2;
			if (opponentDefections[state.step - 3])
				position += 0x4;
			if (myDefections[state.step - 1])
				position += 0x8;
			if (myDefections[state.step - 2])
				position += 0x10;
			if (myDefections[state.step - 3])
				position += 0x20;
			toDefect = ind.genome[position];
		}

		if (toDefect == null)
			throw new RuntimeException("Should have made the decision on whether or not to defect by now");

		return toDefect;
	}
	
}
