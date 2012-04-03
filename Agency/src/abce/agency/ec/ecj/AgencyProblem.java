package abce.agency.ec.ecj;


import ec.*;
import ec.gp.*;
import ec.simple.*;



public class AgencyProblem extends GPProblem implements SimpleProblemForm {

	private static final long	serialVersionUID	= 1L;



	@Override
	public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
		SimpleFitness f = ((SimpleFitness) ind.fitness);
		AgencyGPIndividual agpi = (AgencyGPIndividual) ind;
		f.setFitness(state, (float) agpi.getAgent().getFitness(), false);
		ind.evaluated = true;
		agpi.reset(); // Nullify the link between the agent and the individual
						// at this point as a precaution to avoid leaks or old
						// representations hanging around
	}

}
