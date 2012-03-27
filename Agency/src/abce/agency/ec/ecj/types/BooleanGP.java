package abce.agency.ec.ecj.types;


import ec.gp.*;



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

}
