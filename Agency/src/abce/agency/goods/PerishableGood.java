package abce.agency.goods;

/**
 * Perishable goods are, unless spoil() is overridden, completely spoilt each time step.
 * 
 * @author kkoning
 *
 */
public class PerishableGood extends Good {

	@Override
	public double spoil(double currentQty) {
		return 0;
	}

}
