package ec.agency.rockpaperscissors;

import ec.vector.FloatVectorIndividual;

public class FirstOrderPlayer extends FloatVectorIndividual implements RPSPlayer {
	private static final long serialVersionUID = 1L;

	
	@Override
	public byte play(RPSStimulus stimulus) {
		float rock = genome[0];
		float paper = rock + genome[1];
		float total = paper + genome[2];
		
		float rand = stimulus.random.nextFloat() * total;
		
		if (rand < rock)
			return RPSModel.ROCK;
		if (rand < paper)
			return RPSModel.PAPER;
		else
			return RPSModel.SCISSORS;
	}

}
