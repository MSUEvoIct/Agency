package ec.agency.rockpaperscissors;

import ec.agency.NullIndividual;

public class RockPlayer extends NullIndividual implements RPSPlayer {
	private static final long serialVersionUID = 1L;

	@Override
	public byte play(RPSStimulus stimulus) {
		return RPSModel.ROCK;
	}
	
}
