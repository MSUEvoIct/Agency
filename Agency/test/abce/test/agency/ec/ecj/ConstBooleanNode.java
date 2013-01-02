package abce.test.agency.ec.ecj;

import ec.EvolutionState;
import ec.Problem;
import ec.agency.gp.types.BooleanGP;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class ConstBooleanNode extends GPNode{
	
	boolean b;
	
	public ConstBooleanNode(boolean b){
		this.b = b;
		this.children = new GPNode[0];
	}

	@Override
	public void eval(EvolutionState state, int thread, GPData data, ADFStack adf,
			GPIndividual ind, Problem prob) {
		((BooleanGP) data).value = b;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return Boolean.toString(this.b);
	}
	
	

}
