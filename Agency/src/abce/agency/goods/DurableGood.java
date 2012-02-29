package abce.agency.goods;


/**
 * Durable goods do not spoil.
 * 
 * @author kkoning
 *
 */
public class DurableGood extends Good {

	public DurableGood(String name) {
		setName(name);
	}
	
	@Override
	public double spoil(double currentQty) {
		return currentQty;
	}


}
