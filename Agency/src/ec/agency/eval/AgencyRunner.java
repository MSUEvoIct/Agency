package ec.agency.eval;

public interface AgencyRunner extends ec.Setup {
	public void runSimulations(GroupCreator gc, FitnessListener fl);
}
