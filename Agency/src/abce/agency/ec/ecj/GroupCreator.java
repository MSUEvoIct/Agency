package abce.agency.ec.ecj;

import java.util.Set;

import ec.Individual;

public interface GroupCreator extends ec.Setup {
	public boolean hasNext();
	public Set<Individual> nextGroup();
}
