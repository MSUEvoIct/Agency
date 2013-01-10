package ec.agency.eval;

public interface AgencyRunner extends ec.Setup {
	public void runModel(Runnable model);
	public void finish();
}
