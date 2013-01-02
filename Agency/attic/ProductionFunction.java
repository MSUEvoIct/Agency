package abce.agency.production;

import java.io.Serializable;

public abstract class ProductionFunction implements Serializable {
	private static final long serialVersionUID = 1L;

	public abstract double costOfProducing(double quantity);
}
