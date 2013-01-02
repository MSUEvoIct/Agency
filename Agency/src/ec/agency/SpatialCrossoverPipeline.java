package ec.agency;


import ec.EvolutionState;
import ec.Individual;
import ec.Subpopulation;
import ec.gp.GPIndividual;
import ec.gp.GPInitializer;
import ec.gp.GPNode;
import ec.gp.GPTree;
import ec.gp.koza.CrossoverPipeline;
import ec.spatial.Space;
import ec.util.Parameter;



/**
 * This crossover pipeline is designed to work with subpopulations implementing
 * the Space interface. Crossover only occurs between the individual of interest
 * for the current thread, and genetic material is only recruited from spatially
 * local sources if it is required.
 * 
 * @author ruppmatt
 * 
 */
public class SpatialCrossoverPipeline extends CrossoverPipeline {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	public static final int		INDS_PRODUCED		= 1;
	public static final int		NUM_SOURCES			= 1;



	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		tossSecondParent = true; // Only 1 individual can be returned from a
									// spatial pipeline
	}



	@Override
	public int typicalIndsProduced() {
		return INDS_PRODUCED;
	}



	/**
	 * The method that actually performs the crossover
	 * 
	 * @param state
	 *            The current evolution state
	 * @param thread
	 *            The current thread
	 * @param subpopulation
	 *            The subpopulation being bred
	 * @param products
	 *            Will store the products from the cross-over; the calling
	 *            method's responsibility is to place them correclty.
	 */
	protected void doCrossOver(final EvolutionState state, final int thread, final int subpopulation,
			Individual[] products) {

		GPInitializer initializer = ((GPInitializer) state.initializer);

		// Check tree indexing
		if (tree1 != TREE_UNFIXED && (tree1 < 0 || tree1 >= parents[0].trees.length))
			// uh oh
			state.output
					.fatal("GP Crossover Pipeline attempted to fix tree.0 to a value which was out of bounds of the array of the individual's trees.  Check the pipeline's fixed tree values -- they may be negative or greater than the number of trees in an individual");
		if (tree2 != TREE_UNFIXED && (tree2 < 0 || tree2 >= parents[1].trees.length))
			// uh oh
			state.output
					.fatal("GP Crossover Pipeline attempted to fix tree.1 to a value which was out of bounds of the array of the individual's trees.  Check the pipeline's fixed tree values -- they may be negative or greater than the number of trees in an individual");

		// These are the actual source trees to use
		int tree_source_1 = 0;
		int tree_source_2 = 0;

		// Setup source trees; these may either be (1) randomly fixed or (2)
		// configuration selected
		if (tree1 == TREE_UNFIXED || tree2 == TREE_UNFIXED)
		{
			// pick random trees -- their GPTreeConstraints must be the same
			do
			{
				if (tree1 == TREE_UNFIXED)
					tree_source_1 = (parents[0].trees.length > 1) ? state.random[thread]
							.nextInt(parents[0].trees.length) : 0;
				else
					tree_source_1 = tree1;

				if (tree2 == TREE_UNFIXED)
					tree_source_2 = (parents[1].trees.length > 1) ? state.random[thread]
							.nextInt(parents[1].trees.length) : 0;
				else
					tree_source_2 = tree2;
			} while (parents[0].trees[tree_source_1].constraints(initializer) != parents[1].trees[tree_source_2]
					.constraints(initializer));
		}
		else
		{
			tree_source_1 = tree1;
			tree_source_2 = tree2;
			// make sure the constraints are okay
			if (parents[0].trees[tree_source_1].constraints(initializer) != parents[1].trees[tree_source_2]
					.constraints(initializer)) // uh oh
				state.output
						.fatal("GP Crossover Pipeline's two tree choices are both specified by the user -- but their GPTreeConstraints are not the same");
		}

		// validity results...
		boolean res1 = false;

		// prepare the nodeselectors
		nodeselect1.reset();
		nodeselect2.reset();

		// pick some nodes
		GPNode p1 = null;
		GPNode p2 = null;

		// Find some nodes meeting the requirements
		for (int x = 0; x < numTries; x++)
		{
			// pick a node in individual 1
			p1 = nodeselect1.pickNode(state, subpopulation, thread, parents[0], parents[0].trees[tree_source_1]);

			// pick a node in individual 2
			p2 = nodeselect2.pickNode(state, subpopulation, thread, parents[1], parents[1].trees[tree_source_2]);

			// check for depth and swap-compatibility limits
			res1 = verifyPoints(initializer, p2, p1); // p2 can fill p1's
														// spot -- order is
														// important!

			// did we get something that had both nodes verified?
			// we reject if EITHER of them is invalid. This is what lil-gp
			// does.
			// Koza only has numTries set to 1, so it's compatible as well.
			if (res1)
				break;
		}

		/*
		 * Modified from CrossOverPipeline
		 * 
		 * At this point p1,p2 and res1 may or may not indicate compatible
		 * nodes.
		 */

		// at this point I could check to see if my sources were breeding
		// pipelines -- but I'm too lazy to write that code (it's a little
		// complicated) to just swap one individual over or both over,
		// -- it might still entail some copying. Perhaps in the future.
		// It would make things faster perhaps, not requiring all that
		// cloning.

		// Create some new individuals based on the old ones -- since
		// GPTree doesn't deep-clone, this should be just fine. Perhaps we
		// should change this to proto off of the main species prototype,
		// but we have to then copy so much stuff over; it's not worth it.

		GPIndividual product_1 = (parents[0].lightClone());

		// Fill in various tree information that didn't get filled in there
		product_1.trees = new GPTree[parents[0].trees.length];

		// at this point, p1 or p2, or both, may be null.
		// If not, swap one in. Else just copy the parent.

		for (int tree_ndx = 0; tree_ndx < product_1.trees.length; tree_ndx++)
		{
			/**
			 * Either the tree we're copying is the one that has the potential
			 * for the cross-over or it's not.
			 * If it is the one we're interested in crossing over and the is a
			 * valid (res1 == true) then crossover while copying. Otherwise,
			 * make a copy of the parent source material.
			 */
			if (tree_ndx == tree_source_1 && res1)
			{
				product_1.trees[tree_ndx] = (parents[0].trees[tree_ndx].lightClone());
				product_1.trees[tree_ndx].owner = product_1;
				product_1.trees[tree_ndx].child = parents[0].trees[tree_ndx].child.cloneReplacing(p2, p1);
				product_1.trees[tree_ndx].child.parent = product_1.trees[tree_ndx];
				product_1.trees[tree_ndx].child.argposition = 0;
				product_1.evaluated = false;
			}
			else
			{
				product_1.trees[tree_ndx] = (parents[0].trees[tree_ndx].lightClone());
				product_1.trees[tree_ndx].owner = product_1;
				product_1.trees[tree_ndx].child = (GPNode) (parents[0].trees[tree_ndx].child.clone());
				product_1.trees[tree_ndx].child.parent = product_1.trees[tree_ndx];
				product_1.trees[tree_ndx].child.argposition = 0;
			}
		}

		// add the individual to the population
		products[0] = product_1;

	}



	/**
	 * The bulk of this method is grafted from koza.CrossoverPipeline. The main
	 * change is that the current individual being evaluated must be used as the
	 * first parent individual; the second individual must be a random neighbor.
	 */
	@Override
	public int produce(final int min,
			final int max,
			final int start,
			final int subpopulation,
			final Individual[] inds,
			final EvolutionState state,
			final int thread)

	{

		// TODO: Make sure that subpopulation is spatial.
		Subpopulation this_subpop = state.population.subpops[subpopulation];
		if (!(this_subpop instanceof Space)) {
			state.output.fatal("SpatialCrossoverPipeline expected a subpopulation implementing Space.");
		}
		Space space = (Space) state.population.subpops[subpopulation];

		// how many individuals should we make?
		int n = typicalIndsProduced();

		// should we bother?
		// DO produce children from source -- we've not done so already
		if (!state.random[thread].nextBoolean(likelihood))
			return reproduce(n, start, subpopulation, inds, state, thread, true);

		// Process the upstream source for this node
		// This will set our parent[0]
		sources[0].produce(1, 1, 0, subpopulation, parents, state, thread);

		// Retrieve a local individual (may have been purged by another
		// thread but still available to ours)
		int neighbor_ndx = space.getIndexRandomNeighbor(state, thread, 1);
		if (neighbor_ndx < 0) {
			state.output.fatal("SpatialCrossoverPipeline could not find a neighbor.");
		}
		parents[1] = (GPIndividual) this_subpop.individuals[neighbor_ndx].clone();

		// Hold the products before placing them.
		GPIndividual products[] = new GPIndividual[1];

		// Actually perform the crossover
		doCrossOver(state, subpopulation, thread, products);

		// Place the product tree at the start location
		inds[start] = products[0];

		return 1;

	}
}
