package abce.agency.goods;

public abstract class Good {
	private String name = "Unnamed Good";
	
	private static int idSequence = 0;
	
	private int simulationPriceFloor;
	private int simulationPriceCeiling;
	
	public final int id;
	
	public Good() {
		id = idSequence++;
	}

	/**
	 * Price floors and ceilings in this context are not intended to economically constrain the agents but to
	 * prevent extreme outliers (e.g., values near infinity) from 
	 * 
	 * @return 
	 */
	public int getSimulationPriceFloor() {
		return simulationPriceFloor;
	}

	public void setSimulationPriceFloor(int simulationPriceFloor) {
		this.simulationPriceFloor = simulationPriceFloor;
	}

	public int getSimulationPriceCeiling() {
		return simulationPriceCeiling;
	}

	public void setSimulationPriceCeiling(int simulationPriceCeiling) {
		this.simulationPriceCeiling = simulationPriceCeiling;
	}

	public String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}

	public abstract double spoil(double currentQty);
	
}
