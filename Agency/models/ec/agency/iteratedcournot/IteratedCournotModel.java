package ec.agency.iteratedcournot;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

import ec.EvolutionState;
import ec.Fitness;
import ec.Individual;
import ec.agency.eval.AgencyModel;
import ec.agency.eval.EvaluationGroup;
import ec.agency.io.DataOutputFile;
import ec.agency.io.DelimitedOutFile;
import ec.simple.SimpleFitness;
import ec.util.MersenneTwisterFast;
import ec.util.Parameter;

public class IteratedCournotModel implements AgencyModel {
	private static final long serialVersionUID = 1L;

	private static DataOutputFile out;

	static final Parameter pRoot = new Parameter("eval.model");
	static final Parameter pDemandIntercept = pRoot.push("demandintercept");
	static final Parameter pDemandSlope = pRoot.push("demandslope");
	static final Parameter pSteps = pRoot.push("steps");

	// Operational information about the model
	int job;
	int generation;
	int simulationID;
	int steps;
	int seed;
	MersenneTwisterFast random;

	// Parameters
	float demandIntercept = 0;
	float demandSlope = 0;

	// Operational variables about the agents
	int numAgents;
	Map<IteratedCournotAgent, Integer> agentIDs;
	Map<IteratedCournotAgent, float[]> production;
	Map<IteratedCournotAgent, Float> assets;
	float[] marketPrices;
	float maxQty = 0; // derived from demand parameters

	/**
	 * No-arg constructor required, use ec.setup()
	 */
	public IteratedCournotModel() {
	}

	@Override
	public void setup(EvolutionState evoState, Parameter base) {
		demandIntercept = evoState.parameters.getFloat(pDemandIntercept,
				pDemandIntercept);
		demandSlope = evoState.parameters.getFloat(pDemandSlope, pDemandSlope);
		maxQty = demandIntercept / demandSlope;

		steps = evoState.parameters.getInt(pSteps, pSteps);
		marketPrices = new float[steps];

		agentIDs = new IdentityHashMap<IteratedCournotAgent, Integer>();
		production = new IdentityHashMap<IteratedCournotAgent, float[]>();
		assets = new IdentityHashMap<IteratedCournotAgent, Float>();

		initOutput(evoState);

		job = (Integer) evoState.job[0];
	}

	private void initOutput(EvolutionState evoState) {
		if (IteratedCournotModel.out == null) {
			String fileName = "marketPrices.job" + evoState.job[0];
			IteratedCournotModel.out = new DataOutputFile(fileName);
		}
	}

	public void run() {

		for (int step = 0; step < steps; step++) {
			float price;
			float totalProduction = 0;

			// Determine the production qty for each agent
			for (IteratedCournotAgent ica : agentIDs.keySet()) {
				float[] indProdArray;
				float indProduction;
				ProductionStimulus ps;

				ps = constructProductionStimulus(ica, step);
				indProduction = ica.getProduction(ps);
				if (indProduction < 0)
					indProduction = 0;
				else if (indProduction > maxQty)
					indProduction = maxQty;

				indProdArray = production.get(ica);
				indProdArray[step] = indProduction;
				totalProduction += indProduction;
			}

			// determine the price
			price = getPrice(totalProduction);
			marketPrices[step] = price;

			// hand out revenue to agents
			for (IteratedCournotAgent ica : agentIDs.keySet()) {
				float[] indProdArray;
				float indProduction;
				float revenue;

				indProdArray = production.get(ica);
				indProduction = indProdArray[step];

				revenue = indProduction * price;
				recordRevenue(ica, revenue);
			}

		}

		float avgPrice = 0;
		for (int i = 0; i < marketPrices.length; i++) {
			avgPrice += marketPrices[i];
		}
		avgPrice = avgPrice / marketPrices.length;

		Object[] o = new Object[4];
		o[0] = job;
		o[1] = generation;
		o[2] = simulationID;
		o[3] = avgPrice;

		out.writeTuple(o);
		out.flush();
	}

	private ProductionStimulus constructProductionStimulus(
			IteratedCournotAgent ica, int step) {
		ProductionStimulus ps = new ProductionStimulus();
		ps.step = step;

		if (step > 0) {
			ps.price = marketPrices[step - 1];
			ps.myLastProduction = getProduction(step - 1, ica);
			ps.othersLastProduction = getOtherProduction(step - 1, ica);
		}

		if (step > 1) {
			ps.price2 = marketPrices[step - 2];
			ps.myLastProduction2 = getProduction(step - 2, ica);
			ps.othersLastProduction2 = getOtherProduction(step - 2, ica);
		}

		return ps;
	}

	private void recordRevenue(IteratedCournotAgent ica, float amount) {
		float oldAssets;
		float newAssets;

		if (Float.isInfinite(amount))
			throw new RuntimeException("Earning infinite revenue");
		else if (Float.isNaN(amount))
			throw new RuntimeException("Earning NaN revenue");

		oldAssets = assets.get(ica);
		newAssets = oldAssets + amount;
		assets.put(ica, newAssets);
	}

	public float getPrice(float productionQty) {
		float targetPrice = demandIntercept - demandSlope * productionQty;
		if (targetPrice < 0.00001)
			return 0;
		else
			return targetPrice;
	}

	@Override
	public void setSeed(int seed) {
		this.seed = seed;
		this.random = new MersenneTwisterFast(seed);
	}

	@Override
	public void setGeneration(Integer generation) {
		this.generation = generation;
	}

	@Override
	public Integer getGeneration() {
		return generation;
	}

	@Override
	public void setSimulationID(Integer simulationID) {
		this.simulationID = simulationID;
	}

	@Override
	public Integer getSimulationID() {
		return simulationID;
	}

	@Override
	public Map<Individual, Fitness> getFitnesses() {
		Map<Individual, Fitness> toReturn = new IdentityHashMap<Individual, Fitness>();

		for (IteratedCournotAgent ica : agentIDs.keySet()) {
			Individual ind;
			SimpleFitness fitness = new SimpleFitness();

			float indAssets = assets.get(ica);
			fitness.setFitness(null, indAssets, false);
			ind = (Individual) ica;

			toReturn.put(ind, fitness);
		}

		return toReturn;
	}

	float getTotalProduction(int step) {
		float totalProduction = 0;
		for (IteratedCournotAgent ica : agentIDs.keySet()) {
			float[] prod_array = production.get(ica);
			totalProduction += prod_array[step];
		}
		return totalProduction;
	}

	float getProduction(int step, IteratedCournotAgent ica) {
		float[] prod_array = production.get(ica);
		return prod_array[step];
	}

	float getOtherProduction(int step, IteratedCournotAgent ica) {
		return getTotalProduction(step) - getProduction(step, ica);
	}

	void setProduction(int step, IteratedCournotAgent ica, float prod) {
		float[] prod_array = production.get(ica);
		prod_array[step] = prod;
	}

	@Override
	public void setEvaluationGroup(EvaluationGroup evalGroup) {
		numAgents = 0;

		if (steps <= 0) {
			String msg = "setup() must be called before setEvaluationGroup";
			throw new RuntimeException(msg);
		}

		for (Individual ind : evalGroup.individuals) {
			IteratedCournotAgent ica;

			try {
				ica = (IteratedCournotAgent) ind;
			} catch (Exception e) {
				String msg = "Individual (natively "
						+ ind.getClass().getCanonicalName()
						+ "), must implement IteratedCournotAgent";
				throw new RuntimeException(msg, e);
			}

			agentIDs.put(ica, numAgents++);
			production.put(ica, new float[steps]);
			assets.put(ica, new Float(0));

		}

	}

}
