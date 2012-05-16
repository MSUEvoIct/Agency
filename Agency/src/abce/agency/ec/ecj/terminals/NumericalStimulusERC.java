package abce.agency.ec.ecj.terminals;


import abce.agency.ec.*;
import abce.agency.ec.ecj.*;
import abce.agency.ec.ecj.types.*;
import ec.*;
import ec.gp.*;
import ec.util.*;
import evoict.*;
import evoict.reflection.*;



public class NumericalStimulusERC extends ERC implements Cloneable, SRStimulable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	String						path				= null;
	// RestrictedMethodDictionary dict = null;
	boolean						was_cloned			= false;



	@Override
	public String toString() {
		String to_print = (path == null) ? "unbound" : path;
		String cloned = (was_cloned) ? "*" : "";
		return "NumericalStimulusGP<" + to_print + cloned + ">";
	}



	@Override
	public Object clone() {
		NumericalStimulusERC newnode = (NumericalStimulusERC) (lightClone());
		newnode.path = path;
		// newnode.dict = dict;
		for (int x = 0; x < children.length; x++)
		{
			newnode.children[x] = (children[x].cloneReplacing());
			// if you think about it, the following CAN'T be implemented by
			// the children's clone method. So it's set here.
			newnode.children[x].parent = newnode;
			newnode.children[x].argposition = (byte) x;
		}
		return newnode;
	}



	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {

		StimulusResponse sr = ((StimulusResponseProblem) problem).retrieve();

		// Always reset the dictionary to avoid references to really old
		// dictionary references
		RestrictedMethodDictionary dict = (RestrictedMethodDictionary) sr.dictionary();

		if (path == null) {
			System.err
					.println("This should never happen; paths need to be set *prior* to execution by the Evaluator/Problem.");
			Thread.dumpStack();
			System.exit(1);
		}

		// System.err.println("About to evaluate path: " + path);

		/*
		 * The resolved stored in result value needs to be either a Double or
		 * Integer; because the value is stored in an object, any primitives
		 * (int, double) are converted to Integer or Double. Integer values need
		 * to be converted to a double, which is done automatically, to be
		 * stored in DoubleGP's value field.
		 */
		try {
			Object result = dict.evaluate(path, sr);
			if (result.getClass().isAssignableFrom(Double.class)) {
				((DoubleGP) input).value = (Double) result;
			} else if (result.getClass().isAssignableFrom(Integer.class)) {
				((DoubleGP) input).value = ((Integer) result);
			} else {
				throw new UnresolvableException("Incorrect type: " + result.getClass().getCanonicalName()
						+ " from path "
						+ path);
			}
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
		path = null;
	}



	@Override
	public boolean nodeEquals(GPNode node) {
		if (node instanceof NumericalStimulusERC) {
			NumericalStimulusERC comapre_to = (NumericalStimulusERC) node;
			if (comapre_to.path == null && path == null) {
				return true;
			}
			else if (path.equals(comapre_to.path)) {
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
		String to_encode = (this.path == null) ? "unbound" : this.path;
		return Code.encode(to_encode);
	}



	@Override
	public String getStimulusPath() {
		return path;
	}



	@Override
	public void setStimulusPath(EvolutionState state, int thread, RestrictedMethodDictionary dict) {
		String[] possible = dict.enumerate();
		int ndx = state.random[thread].nextInt(possible.length);
		path = possible[ndx];
	}
}
