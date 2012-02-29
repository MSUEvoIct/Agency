package abce.agency.goods;

public abstract class Good {
	private String name = "Unnamed Good";

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
