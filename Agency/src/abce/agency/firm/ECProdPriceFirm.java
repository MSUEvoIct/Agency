package abce.agency.firm;


import java.util.ArrayList;

import sim.engine.SimState;
import abce.agency.Market;
import abce.agency.actions.ProductionAction;
import abce.agency.actions.SetPriceAction;
import abce.agency.ec.EvolvableAgent;
import abce.agency.ec.StimulusResponse;
import abce.agency.firm.sr.FirmPricingSR;
import abce.agency.firm.sr.FirmProductionSR;
import abce.agency.goods.Good;
import abce.agency.reflection.Stimulus;



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

	/**
	 * 
	 */
	@Stimulus(name = "LastPriceScaling")
	double											last_price_scale	= 1.0;

	@Stimulus(name = "LastProduction")
	Double											production			= null;

	ArrayList<Class<? extends FirmPricingSR>>		sr_pricing			= new ArrayList<Class<? extends FirmPricingSR>>();
	ArrayList<Class<? extends FirmProductionSR>>	sr_production		= new ArrayList<Class<? extends FirmProductionSR>>();



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
		for (Market m : this.markets) {
			Good g = m.good;
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
	}



	@Override
	public void produce() {
		for (Market m : this.markets) {
			Good g = m.good;
			try {
				if (production == null)
					production = m.getNumberOfPeople() / m.getNumberOfFirms();
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
	}



	public void scalePrice(Market m, Good g, double perc) {
		double scale = 1.0 + (perc / 100.0);
		if (scale < 0.0) {
			last_price_scale = 1.0;
			return;
		} else {
			last_price_scale = scale;
			double current_price = prices.get(g);
			SetPriceAction spa = new SetPriceAction(this, g, current_price * scale);
			actualize(spa);
		}
	}



	public void scaleProduction(Market m, Good g, double perc) {
		double scale = 1.0 + (perc / 100.0);

		if (scale < 0.0) {
			scale = 1.0;
		}

		double new_production = production * scale;
		ProductionAction pa = new ProductionAction(this, g, new_production);
		pa.process();
		actualize(pa);
		production = new_production;
	}



	@Override
	public void step(SimState state) {
		produce();
		price();
		super.step(state);
	}
}
