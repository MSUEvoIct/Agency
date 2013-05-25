package ec.agency.prisonersdilemma;

import ec.vector.BitVectorIndividual;

/**
 * Agent that plays the iterated prisoners dilemma game according to a GA.
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
 */public class BruteMemoryPrisoner extends BitVectorIndividual implements Prisoner {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean defect(InterrogationStimulus is) {
		Boolean toDefect = null;
		
		// if step 1, just take our decision directly from the genome.
		if (is.step == 0) {
			toDefect = genome[0x54];
		}
		
		if (is.step == 1) {
			int position = 0x50;
			if (is.opponentsPlays.get(0))
				position += 0x1;
			if (is.myPlays.get(0))
				position += 0x2;
			toDefect = genome[position];
		}
		
		if (is.step == 2) {
			int position = 0x40;
			if (is.opponentsPlays.get(1))
				position += 0x1;
			if (is.opponentsPlays.get(0))
				position += 0x2;
			if (is.myPlays.get(1))
				position += 0x4;
			if (is.myPlays.get(0))
				position += 0x8;
			toDefect = genome[position];
		}
		
		if (is.step >= 3) {
			int position = 0x0;
			
			if (is.opponentsPlays.get(is.step - 1))
				position += 0x1;
			if (is.opponentsPlays.get(is.step - 2))
				position += 0x2;
			if (is.opponentsPlays.get(is.step - 3))
				position += 0x4;
			if (is.myPlays.get(is.step - 1))
				position += 0x8;
			if (is.myPlays.get(is.step - 2))
				position += 0x10;
			if (is.myPlays.get(is.step - 3))
				position += 0x20;
			toDefect = genome[position];
		}

		if (toDefect == null)
			throw new RuntimeException("Should have made the decision on whether or not to defect by now");

		return toDefect;

	}

}
