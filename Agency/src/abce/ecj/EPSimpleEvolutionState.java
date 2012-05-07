package abce.ecj;


import java.util.*;

import ec.simple.*;
import ec.util.*;
import ec.util.Parameter;
import evoict.*;
import evoict.ep.*;



/**
 * EASimpleEvolutionState is a simple modification of ECJ's SimpleEvoutionState
 * class that uses an EventProcedurenManager (EPM). EPMs allow for
 * EventProcedures to be configured in an external file. EventProcedures (EPs)
 * are triggered when certain events (like generation times) occur.
 * 
 * Additionally, parameters in the parameter database may be overridden by
 * command line arguments in the form "--eset [key] [value]" where key is some
 * key in the parameter database and value is the new value.
 * 
 * In EASimpleEvolutionState, three types of events occurring over generation
 * time are considered:
 * 
 * PreEvaluation -- perform the event action before the evaluator is executed
 * PostEvaluation -- "" after the evaluator is executed
 * PostBreeding -- "" after the breeder is executed
 * 
 * The configuration file (specified by the value following the command line
 * argument --events) contains event actions, one per line, with hash marks (#)
 * as comments.
 * 
 * Example event action in event action configuration file:
 * 
 * PreEvaluation 0:10:end some.package.PrintPopulation path_to_output_fie
 * 
 * In this case, PreEvaluation is the name of the triggering event. The range
 * value 0:10:end triggers the event every ten generations until the end of the
 * experiment. Range values take the form start:interval:end. In the even the
 * trigger should happen only once, a single numerical value can be specified
 * instead of the entire range. The literal "end" means the event has no
 * stopping point (inclusive).
 * 
 * @author ruppmatt
 * 
 */
public class EPSimpleEvolutionState extends SimpleEvolutionState {

	/**
	 * 
	 */
	private static final long					serialVersionUID	= 1L;

	public ECJEventProceedureManager			event_manager		= new ECJEventProceedureManager();

	public boolean								first_run			= true;

	public ArrayList<EventProcedureDescription>	domain_events		= new ArrayList<EventProcedureDescription>();



	@Override
	public void startFresh() {
		updateParameterDatabase();
		try {
			event_manager.buildFromFile(this.parameters.getString(new Parameter("event_file"), null));
		} catch (BadParameterException e) {
			System.exit(1);
			e.printStackTrace();
		} catch (BadConfiguration e) {
			System.exit(1);
			e.printStackTrace();
		}
		super.startFresh();
	}



	public void updateParameterDatabase() {

	}



	/**
	 * Evolve method with calls to the event_manager to process procedures for
	 * the current generation (if available).
	 * 
	 * @return
	 * @throws InternalError
	 */
	@Override
	public int evolve()
	{
		// Always reset the domain model events prior to the start of the
		// current generation
		domain_events.clear();

		if (first_run) {
			updateArguments();
		}

		if (generation > 0)
			output.message("Generation " + generation);

		// Process procedures prior to evaluation
		event_manager.process(ECJEventProceedureManager.EVENT_PRE_EVALUATION, generation, this);

		// EVALUATION
		statistics.preEvaluationStatistics(this);
		evaluator.evaluatePopulation(this);
		statistics.postEvaluationStatistics(this);

		// Process procedures after evaluation
		event_manager.process(ECJEventProceedureManager.EVENT_POST_EVALUATION, generation, this);

		// SHOULD WE QUIT?
		if (evaluator.runComplete(this) && quitOnRunComplete)
		{
			event_manager.finish();
			output.message("Found Ideal Individual");
			return R_SUCCESS;
		}

		// SHOULD WE QUIT?
		// @MRR Allows for numGernations + 1
		if (generation == numGenerations)
		{
			event_manager.finish();
			return R_FAILURE;
		}

		// PRE-BREEDING EXCHANGING
		statistics.prePreBreedingExchangeStatistics(this);
		population = exchanger.preBreedingExchangePopulation(this);
		statistics.postPreBreedingExchangeStatistics(this);

		String exchangerWantsToShutdown = exchanger.runComplete(this);
		if (exchangerWantsToShutdown != null)
		{
			output.message(exchangerWantsToShutdown);
			/*
			 * Don't really know what to return here. The only place I could
			 * find where runComplete ever returns non-null is
			 * IslandExchange. However, that can return non-null whether or
			 * not the ideal individual was found (for example, if there was
			 * a communication error with the server).
			 * 
			 * Since the original version of this code didn't care, and the
			 * result was initialized to R_SUCCESS before the while loop, I'm
			 * just going to return R_SUCCESS here.
			 */

			return R_SUCCESS;
		}

		// BREEDING
		statistics.preBreedingStatistics(this);

		population = breeder.breedPopulation(this);

		// POST-BREEDING EXCHANGING
		statistics.postBreedingStatistics(this);

		// Evaluate procedures after breeding
		event_manager.process(event_manager.EVENT_POST_BREEDING, generation, this);

		// POST-BREEDING EXCHANGING
		statistics.prePostBreedingExchangeStatistics(this);
		population = exchanger.postBreedingExchangePopulation(this);
		statistics.postPostBreedingExchangeStatistics(this);

		// INCREMENT GENERATION AND CHECKPOINT
		generation++;
		if (checkpoint && generation % checkpointModulo == 0)
		{
			output.message("Checkpointing");
			statistics.preCheckpointStatistics(this);
			Checkpoint.setCheckpoint(this);
			statistics.postCheckpointStatistics(this);
		}

		return R_NOTDONE;
	}



	/**
	 * Checks the command line arguments passed into main and updates the
	 * parameter database
	 */
	public void updateArguments() {

	}

}
