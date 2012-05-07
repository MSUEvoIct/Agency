package abce.agency.ec;


public interface EvolvableAgent {

	public void addSR(Class<? extends StimulusResponse> cl);



	public void emit(StimulusResponse sr);



	public double getFitness();
}
