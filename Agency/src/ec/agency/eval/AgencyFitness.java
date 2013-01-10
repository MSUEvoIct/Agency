package ec.agency.eval;

import ec.Fitness;
import ec.util.Parameter;

/**
 * Not an ECJ fitness
 * 
 * @author kkoning
 *
 */
public class AgencyFitness extends ec.Fitness {
	private static final long serialVersionUID = 1L;

	@Override
	public Parameter defaultBase() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean betterThan(Fitness arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean equivalentTo(Fitness arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public float fitness() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isIdealFitness() {
		// TODO Auto-generated method stub
		return false;
	}

}
