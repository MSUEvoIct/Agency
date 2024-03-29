package ec.agency.gp.types;


import ec.gp.GPData;



public class BooleanGP extends GPData implements Valuable {

	public boolean	value;



	@Override
	public void copyTo(GPData gpd) {
		((BooleanGP) gpd).value = this.value;
	}



	@Override
	public Object value() {
		return new Boolean(value);
	}



	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
