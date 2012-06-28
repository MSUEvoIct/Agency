package abce.ecj;


import ec.EvolutionState;
import ec.gp.GPIndividual;
import ec.util.Parameter;



public class TaggedGPIndividual extends GPIndividual implements TaggedIndividual {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public byte					tag;



	@Override
	public void setup(final EvolutionState state, final Parameter base) {
		super.setup(state, base);
		tag = TaggedIndividual.UNKNOWN;
	}



	@Override
	public Object clone() {
		TaggedGPIndividual cl = (TaggedGPIndividual) super.clone();
		cl.tag = tag;
		return cl;
	}



	@Override
	public GPIndividual lightClone() {
		TaggedGPIndividual cl = (TaggedGPIndividual) super.lightClone();
		cl.tag = tag;
		return cl;
	}



	@Override
	public byte getTag() {
		return tag;
	}



	@Override
	public void setTag(byte tag) {
		this.tag = tag;
	}

}
