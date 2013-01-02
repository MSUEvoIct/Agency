package abce.agency.ec.ecj;


import ec.EvolutionState;
import ec.Problem;
import ec.util.Parameter;



public abstract class MASProblem extends Problem implements CallableGroupProblemForm {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// The name of the domain problem's parameter base
	public String				domain_name;

	// The file path of the domain configuration file
	public String				domain_config;

	public static final String	P_NAME				= "name";
	public static final String	P_CONFIG			= "config";

	// Instance-specific members; these should be reset on clone
	protected EvolutionState	state;
	protected EvaluationGroup	group;
	protected int				threadnum;



	@Override
	public void setup(final EvolutionState state, Parameter base) {
		super.setup(state, base);
		Parameter defbase = defaultBase();
		domain_name = state.parameters.getString(base.push(P_NAME), defbase.push(P_NAME));
		domain_config = state.parameters.getString((new Parameter(domain_name)).push(P_CONFIG), defbase.push(P_CONFIG));
	}



	@Override
	public Object clone() {
		MASProblem obj = (MASProblem) super.clone();
		obj.reset();
		return obj;
	}



	/**
	 * Reset all non-prototype fields.
	 */
	@Override
	public void reset() {
		state = null;
		group = null;
		threadnum = -1;
	}



	@Override
	/**
	 * Evaluate the problem.
	 * 
	 * @param base_state
	 * 		Should be able to be casted to EPSimpleEvolutionState
	 * @param ind
	 * 		A list of individuals to include in the model
	 * @param updateFitness
	 * 		Whether or not the fitnesses should be updated for each individual
	 * @param subpops
	 * 		The subpopulations in which the individuals belong
	 * @param threadnum
	 * 		The thread number
	 */
	public void setupForEvaluation(EvolutionState state, EvaluationGroup g, int threadnum) {
		this.state = state;
		this.group = g;
		this.threadnum = threadnum;
	}

}
