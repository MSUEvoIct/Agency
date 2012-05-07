package abce.agency.firm;


import java.util.*;

import abce.agency.*;
import abce.agency.actions.*;
import abce.agency.ec.*;
import abce.agency.firm.sr.*;
import abce.agency.goods.*;
import evoict.reflection.*;



public abstract class ECFirm extends Firm implements EvolvableAgent {

	/**
	 * 
	 */
	private static final long						serialVersionUID		= 1L;

	/**
	 * 
	 */
	@Stimulus(name = "LastPriceScaling")
	double											last_price_scale		= 1.0;

	@Stimulus(name = "LastProductionScaling")
	double											last_production_scale	= 1.0;

	ArrayList<Class<? extends FirmPricingSR>>		sr_pricing				= new ArrayList<Class<? extends FirmPricingSR>>();
	ArrayList<Class<? extends FirmProductionSR>>	sr_production			= new ArrayList<Class<? extends FirmProductionSR>>();



	@SuppressWarnings("unchecked")
	@Override
	public void addSR(Class<? extends StimulusResponse> cl) {
		Class<?>[] cls = cl.getInterfaces();
		for (Class<?> c : cls) {
			System.err.println(c.getCanonicalName());
			if (c.isAssignableFrom(FirmPricingSR.class)) {
				sr_pricing.add((Class<? extends FirmPricingSR>) cl);
			} else if (c.isAssignableFrom(FirmProductionSR.class)) {
				sr_production.add((Class<? extends FirmProductionSR>) cl);
			}
		}
	}



	@Override
	protected void price() {
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
	protected void produce() {
		for (Market m : this.markets) {
			Good g = m.good;
			try {

				FirmProductionSR sr;
				for (Class<? extends FirmProductionSR> cl : sr_production) {
					sr = cl.newInstance();
					sr.setup(this, m, g);
					System.err.println(sr.getClass().getCanonicalName());
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
			setPrice(g, current_price * scale);
		}
	}



	public void scaleProduction(Market m, Good g, double perc) {
		double scale = 1.0 + (perc / 100.0);

		if (scale < 0.0) {
			scale = 1.0;
		}

		last_production_scale = scale;
		double last_production = getLastProduction(g);
		double new_production = last_production * scale;
		System.err.println("Produce: " + new_production);
		ProductionAction pa = new ProductionAction(this, g, new_production);
		pa.process();
		setLastProduction(g, new_production);
	}
}
