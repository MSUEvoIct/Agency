package ec.agency.gp.types;


import ec.gp.GPData;



public class DoubleGP extends GPData implements Valuable {

	public double	value;



	@Override
	public void copyTo(GPData gpd) {
		((DoubleGP) gpd).value = this.value;
	}



	@Override
	public Object value() {
		return new Double(value);
	}



	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
