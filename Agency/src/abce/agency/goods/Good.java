package abce.agency.goods;

public abstract class Good {
	private String name = "Unnamed Good";
	
	private static int idSequence = 0;
	public final int id;
	
	public Good() {
		id = idSequence++;
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
