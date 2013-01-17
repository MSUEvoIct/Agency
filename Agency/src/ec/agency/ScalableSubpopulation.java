package ec.agency;

public interface ScalableSubpopulation {
	public int getSubpopulationGroup();
	public int getMinSize();
	public void setTargetSize(int numIndividuals);
}
