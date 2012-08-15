package abce.agency.firm;


import java.util.ArrayList;
import java.util.LinkedHashMap;

import sim.engine.SimState;
import abce.agency.Market;
import abce.agency.actions.ProductionAction;
import abce.agency.actions.SetPriceAction;
import abce.agency.ec.EvolvableAgent;
import abce.agency.ec.StimulusResponse;
import abce.agency.firm.sr.FirmPricingSR;
import abce.agency.firm.sr.FirmProductionSR;
import abce.agency.goods.Good;
import abce.util.reflection.Stimulus;



/**
 * ECFirm provides a general interface to any evolutionary computation system
 * that uses StimulusResponse objects.
 * 
 * @author ruppmatt
 * 
 */
public abstract class ECProdPriceFirm extends Firm implements ProducingPricingFirm, EvolvableAgent {

	/**
	 * 
	 */
	private static final long						serialVersionUID	= 1L;

	ArrayList<Class<? extends FirmPricingSR>>		sr_pricing			= new ArrayList<Class<? extends FirmPricingSR>>();
	ArrayList<Class<? extends FirmProductionSR>>	sr_production		= new ArrayList<Class<? extends FirmProductionSR>>();
	
	LinkedHashMap<Good,Double> next_price = new LinkedHashMap<Good,Double>();
	LinkedHashMap<Good,Double> next_production = new LinkedHashMap<Good,Double>();



	/**
	 * Add a stimulus response class to the ECFirm; these classes get put into
	 * specific (e.g. pricing or production) queues for SR emissions.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addSR(Class<? extends StimulusResponse> cl) {
		Class<?>[] cls = cl.getInterfaces();
		for (Class<?> c : cls) {
			if (c.isAssignableFrom(FirmPricingSR.class)) {
				sr_pricing.add((Class<? extends FirmPricingSR>) cl);
			} else if (c.isAssignableFrom(FirmProductionSR.class)) {
				sr_production.add((Class<? extends FirmProductionSR>) cl);
			}
		}
	}



	@Override
	public void price() {
		
		//Setup the next price for all goods in all markets
		next_price.clear();
		for (Market m : this.markets) {
			Good g = m.good;
			//Setup base price for next adjustment
			if (!next_price.containsKey(g)){
				next_price.put(g, prices.get(g));
			}
			try {
				FirmPricingSR sr;
				for (Class<? extends FirmPricingSR> cl : sr_pricing) {
					sr = cl.newInstance();
					sr.setup(this, m, g);
					emit(sr);
				}
			} catch (Exception e) {
				System.err.println("Unable to produce stimulus response.");
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		//Try to actualize the price for all goods in all markets
		for (Market m : this.markets){
			Good g = m.good;
			SetPriceAction spa = new SetPriceAction(this, g, next_price.get(g));
			spa.process();
		}
	}



	@Override
	public void produce() {
		
		next_production.clear();
		
		//Setup the production for all goods
		for (Market m : this.markets) {
			Good g = m.good;
			//Setup base price for next adjustment based on last production
			if (!next_production.containsKey(g)){
				next_production.put(g, last_production.get(g));
			}
			try {
				FirmProductionSR sr;
				for (Class<? extends FirmProductionSR> cl : sr_production) {

					sr = cl.newInstance();
					sr.setup(this, m, g);
					emit(sr);
				}
			} catch (Exception e) {
				System.err.println("Unable to produce stimulus response.");
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		//Try to actualize the production of all goods
		for (Market m: this.markets){
			Good g = m.good;
			ProductionAction pa = new ProductionAction(this, g, next_production.get(g));
			pa.process();
		}
	}



	public void scalePrice(Market m, Good g, double perc) {
		double scale = 1.0 + (perc / 100.0);
		double current = next_price.get(g);
		next_price.put(g,  current*scale);
		
	
	}



	public void scaleProduction(Market m, Good g, double perc) {
		double scale = 1.0 + (perc / 100.0);
		if (scale < 0.0) {
			scale = 1.0;
		}
		double new_production = next_production.get(g) * scale;
	}



	@Override
	public void step(SimState state) {
		produce();
		price();
		super.step(state);
	}
}
