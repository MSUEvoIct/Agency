package abce.agency.ec.ecj.test;

import abce.agency.ec.ecj.types.DoubleGP;
import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

class ConstDoubleNode extends GPNode{

	double d;
	
	public ConstDoubleNode(double d){
		this.d = d;
		this.children = new GPNode[0];
	}
	
	@Override
	public void eval(EvolutionState state, int thread, GPData result,
			ADFStack stack, GPIndividual ind, Problem prob) {
		DoubleGP dgp = (DoubleGP) result;
		dgp.value = d;
	}

	@Override
	public String toString() {
		return Double.toString(d);
	}
	
}

