package abce.agency.goods;

/**
 * Perishable goods are, unless spoil() is overridden, completely spoilt each time step.
 * 
 * @author kkoning
 *
 */
public class PerishableGood extends Good {

	public double fracSpoil = 0.0;
	
	public PerishableGood(String name, double s){
		setName(name);
		setFracSpoil(s);
	}
	
	public void setFracSpoil(double s){
		fracSpoil = s;
	}
	
	public double getFracSpoil(){
		return fracSpoil;
	}
	
	@Override
	public double spoil(double currentQty) {
		return (1-fracSpoil) * currentQty;
	}

}
