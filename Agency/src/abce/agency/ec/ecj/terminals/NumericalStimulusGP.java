package abce.agency.ec.ecj.terminals;


import abce.agency.ec.*;
import ec.*;
import ec.gp.*;
import ec.util.*;
import evoict.reflection.*;



public class NumericalStimulusGP extends ERC {

	String						path	= null;
	RestrictedMethodDictionary	dict	= null;



	@Override
	public String toString() {
		return "NumericalStimulusGP";
	}



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {

		StimulusResponse sr = (StimulusResponse) problem;

		// Always reset the dictionary to avoid references to really old
		// dictionaries
		dict = (RestrictedMethodDictionary) sr.dictionary();

		if (path == null) {
			mutate(state, thread);
		}

	}



	@Override
	public void resetNode(EvolutionState state, int thread) {
		if (dict == null) {
			return;
		} else {
			path = dict.getRandomPath(state.random[thread]);
		}
	}



	@Override
	public boolean nodeEquals(GPNode node) {
		if (node instanceof NumericalStimulusGP) {
			NumericalStimulusGP comapre_to = (NumericalStimulusGP) node;
			if (path.equals(comapre_to.path)) {
				return true;
			}
		}
		return false;
	}



	@Override
	public String encode() {
		return Code.encode(this.path);
	}
}
