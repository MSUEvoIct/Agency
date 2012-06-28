package abce.models.iteratedpd;

import sim.engine.SimState;
import sim.engine.Steppable;

public interface IteratedPDAgent extends Steppable {
	public boolean defected(SimState state, int stepsAgo);
	public void earn(double amount);
	public double getTotalRevenue();
	
}
