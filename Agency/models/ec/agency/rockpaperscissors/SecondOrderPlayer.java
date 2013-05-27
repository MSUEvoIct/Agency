package ec.agency.rockpaperscissors;

import ec.vector.FloatVectorIndividual;

public class SecondOrderPlayer extends FloatVectorIndividual implements RPSPlayer {
	private static final long serialVersionUID = 1L;

	@Override
	public byte play(RPSStimulus stimulus) {
		int offset = 0;
		if (stimulus.step > 0) {
			offset = 3;
			offset += 3 * stimulus.oppHistory[stimulus.step-1];
		}
		
		float rock = genome[offset];
		float paper = rock + genome[offset + 1];
		float total = paper + genome[offset + 2];
		
		float rand = stimulus.random.nextFloat() * total;
		
		if (rand < rock)
			return RPSModel.ROCK;
		if (rand < paper)
			return RPSModel.PAPER;
		else
			return RPSModel.SCISSORS;
	}

}
