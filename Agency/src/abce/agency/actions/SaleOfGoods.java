package abce.agency.actions;

import java.io.Serializable;

import abce.agency.consumer.Consumer;
import abce.agency.firm.Firm;
import abce.agency.goods.Good;

public class SaleOfGoods extends SimulationAction {
	private static final long serialVersionUID = 1L;

	public final Consumer buyer;
	public final Firm seller;
	public final Good good;
	public final double quantity;
	public final double price;
	
	/**
	 * Create a transaction for the sale of goods where the quantity purchased is equal
	 * to the number of persons represented by the consumer agent.
	 * 
	 * @param c The consumer agent buying the good
	 * @param f The firm selling the good
	 * @param g The good
	 * @param price The price of the good
	 */
	public SaleOfGoods(Consumer c, Firm f, Good g, double price) {
		this(c,f,g,c.getPopulation(),price);
	}
	
	/**
	 * Create a transaction for the sale of goods where the quantity purchased is 
	 * explicitly specified.  Keep in mind the fact that the consumer agent represents
	 * an arbitrary nunber of natural persons.
	 * 
	 * @param c The consumer agent buying to good
	 * @param f The firm selling the good
	 * @param g The good
	 * @param quantity The total quantity of the good, spread across all persons
	 * 							represented by the consumer agent
	 * @param price The price of the good
	 */
	public SaleOfGoods(Consumer c, Firm f, Good g, double price, double quantity) {
			this.buyer = c;
			this.seller = f;
			this.good = g;
			this.quantity = quantity;
			this.price = price;
	}
	
	@Override
	protected String describe() {
		return "Firm " + seller + " selling " + quantity + " of good " + good + " to consumer " + buyer + " at price " + price; 
	}

	@Override
	protected void execute() {
		buyer.execute(this);
		seller.execute(this);
	}

	@Override
	protected boolean verify() {
		return true;  // TODO: How should this be verified?
	}

}
