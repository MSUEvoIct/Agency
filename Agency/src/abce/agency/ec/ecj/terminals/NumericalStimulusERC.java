package abce.agency.ec.ecj.terminals;


import abce.agency.ec.*;
import ec.*;
import ec.gp.*;
import ec.util.*;
import evoict.*;
import evoict.reflection.*;



public class NumericalStimulusERC extends ERC {

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
		// dictionary references
		dict = (RestrictedMethodDictionary) sr.dictionary();

		if (path == null) {
			resetNode(state, thread);
		}

		try {
			dict.evaluate(path, problem);
		} catch (UnresolvableException e) {
			System.err.println("Unable to resolve method path: " + path + " with root object "
					+ problem.getClass().getCanonicalName());
			System.err.println(e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

	}



	@Override
	public void mutateERC(EvolutionState state, int thread) {
		resetNode(state, thread);
	}



	@Override
	public void resetNode(EvolutionState state, int thread) {
		if (dict == null) {
			return;
		} else {
			String[] possible = dict.enumerate();
			int len = possible.length;
			path = possible[state.random[thread].nextInt(len)];
		}
	}



	@Override
	public boolean nodeEquals(GPNode node) {
		if (node instanceof NumericalStimulusERC) {
			NumericalStimulusERC comapre_to = (NumericalStimulusERC) node;
			if (path.equals(comapre_to.path)) {
				return true;
			}
		}
		return false;
	}



	@Override
	/**
	 * TODO: Not sure about what's being read initiallly; but I followed the pattern to create this magic.
	 */
	public boolean decode(DecodeReturn dret) {
		int pos = dret.pos;
		String data = dret.data;
		Code.decode(dret);
		if (dret.type != DecodeReturn.T_STRING) {
			dret.data = data;
			dret.pos = pos;
			return false;
		}
		this.path = dret.data;
		return true;
	}



	@Override
	public String encode() {
		return Code.encode(this.path);
	}
}
