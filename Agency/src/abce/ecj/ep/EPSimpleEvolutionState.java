package abce.ecj.ep;


import java.util.ArrayList;
import java.util.LinkedHashMap;

import abce.agency.ec.ecj.Debugger;
import abce.util.events.EventProcedureDescription;
import abce.util.events.Interval;
import abce.util.io.FileManager;
import ec.simple.SimpleEvolutionState;
import ec.util.Checkpoint;
import ec.util.Parameter;



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
	private static final long					serialVersionUID		= 1L;

	public ECJEventProcedureManager				event_manager			= new ECJEventProcedureManager();

	public ArrayList<EventProcedureDescription>	domain_events			= new ArrayList<EventProcedureDescription>();

	public LinkedHashMap<String, String>		domain_config_overrides	= new LinkedHashMap<String, String>();

	public boolean								first_run				= true;

	public transient FileManager				file_manager;

	public Debugger								debug;

	public static final String					P_OUTPUT_DIR			= "output_dir";
	public static final String					P_EVENT_FILE			= "event_file";



	@Override
	public void startFromCheckpoint() {
		// The file manager wasn't serialized. It needs to be re-created.
		file_manager = new FileManager();

		try {
			file_manager.initialize(this.getString(new Parameter(P_OUTPUT_DIR), null));
		} catch (Exception e) {
			e.printStackTrace();
			this.output.fatal("Unable to initialize file manager.");
		}

		debug = new Debugger();
		debug.setup(this, null);
	}



	private String getString(Parameter parameter, Object object) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void startFresh() {
		updateParameterDatabase();
		try {
			event_manager.buildFromFile(this.parameters.getString(new Parameter(P_EVENT_FILE), null));
		} catch (Exception e) {
			e.printStackTrace();
			this.output.fatal("Unable to build event manager.");
		}
		file_manager = new FileManager();
		;
		try {
			String dir = this.parameters.getString(new Parameter(P_OUTPUT_DIR), null);
			file_manager.initialize(dir);
		} catch (Exception e) {
			e.printStackTrace();
			this.output.fatal("Unable to intiailize file manager.");
		}

		debug = new Debugger();
		debug.setup(this, null);

		super.startFresh();
	}



	public void updateParameterDatabase() {

		String tok = null; // current token
		String path = null; // holds parameter database path
		String setting = null; // holds parameter database setting
		int ndx = 0;
		int nargs = this.runtimeArguments.length;
		final String[] args = this.runtimeArguments;

		// Just to see what's been passed
		StringBuilder sb = new StringBuilder();
		sb.append("Runtime Arguments: ");
		for (String s : args) {
			sb.append(s + " ");
		}
		this.output.message(sb.toString());

		while (ndx < nargs) {
			tok = args[ndx];
			if (tok.equals("-dset")) {
				if (ndx + 2 < nargs) {
					path = args[ndx + 1];
					setting = args[ndx + 2];
					domain_config_overrides.put(path, setting);
					ndx += 3;
				} else {
					this.output.fatal("Invalid domain configuration setting.  Should be in form -sset Path Value");
				}
			} else {
				ndx++;
			}
		}
	}



	public void finish() {
		file_manager.closeAll();
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
		
		double generation_cxt = (generation == numGenerations - 1) ? Interval.ATEND : generation;

		if (generation > 0)
			output.message("Generation " + generation);

		// Process procedures prior to evaluation
		event_manager.process(ECJEventProcedureManager.EVENT_PRE_EVALUATION, generation_cxt, this);

		// Prepare any Problem domain events
		event_manager.process(ECJEventProcedureManager.EVENT_DOMAIN, generation_cxt, this);

		// EVALUATION
		statistics.preEvaluationStatistics(this);
		evaluator.evaluatePopulation(this);
		statistics.postEvaluationStatistics(this);

		// Process procedures after evaluation
		event_manager.process(ECJEventProcedureManager.EVENT_POST_EVALUATION, generation_cxt, this);

		// SHOULD WE QUIT?
		if (evaluator.runComplete(this) && quitOnRunComplete)
		{
			event_manager.finish();
			output.message("Found Ideal Individual");
			return R_SUCCESS;
		}

		// SHOULD WE QUIT?
		if (generation == numGenerations - 1)
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
		event_manager.process(event_manager.EVENT_POST_BREEDING, generation_cxt, this);

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

}
