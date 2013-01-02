package abce.agency.production;

public class ConstantCostProductionFunction extends ProductionFunction {
	private static final long serialVersionUID = 1L;

	private double cost = 0.0;
	
	public ConstantCostProductionFunction(double cost) {
		// Cost must be positive
		if (cost <= 0)
			throw new RuntimeException("Production cost must be positive, was " + cost);
		this.cost = cost;
	}
	
	@Override
	public double costOfProducing(double quantity) {
		return quantity * cost;
	}
	
}
