package abce.models.io.iterated.cournot;

import sim.engine.SimState;
import abce.agency.firm.Firm;
import ec.Fitness;
import ec.Individual;
import ec.agency.iteratedcournot.IteratedCournotAgent;
import ec.agency.iteratedcournot.IteratedCournotModel;
import ec.vector.BitVectorIndividual;

public class IteratedCournotAgentGA extends Firm implements
		IteratedCournotAgent {
	private static final long serialVersionUID = 1L;

	public static final long oep1 = 0x2;
	public static final long oep2 = 0x4;
	public static final long ogp1 = 0x8;
	public static final long ogp2 = 0x10;
	public static final long omp1 = 0x20;
	public static final long omp2 = 0x40;
	public static final long orp1 = 0x80;
	public static final long orp2 = 0x100;
	public static final long mru1 = 0x200;
	public static final long mru2 = 0x400;
	public static final long mri1 = 0x800;
	public static final long mri2 = 0x1000;
	
	public final double productionInterval = 1.0;
	
	public double[] production = new double[trackingPeriods];
	public double[] revenue = new double[trackingPeriods];
	public double totalRevenue = 0.0;
	
	public BitVectorIndividual ind;
	
	public IteratedCournotAgentGA(BitVectorIndividual ind) {
		this.ind = ind;
		// test to see if initial production quantities change things...
		for (int i = 0; i < production.length; i++)
			production[i] = 45;
	}
	
	@Override
	public void step(SimState state) {
		super.step(state);
		IteratedCournotModel ics = (IteratedCournotModel) state;
		
		// states to determine
		boolean oppEqualProduction1, oppEqualProduction2;
		boolean oppGreaterProduction1, oppGreaterProduction2;
		boolean oppMaintainedProduction1, oppMaintainedProduction2;
		boolean oppRaisedProduction1, oppRaisedProduction2;
		boolean myRevenueUnchanged1, myRevenueUnchanged2;
		boolean myRevenueIncreased1, myRevenueIncreased2;

		// determine those states
		// compare production quantities
		double myProd1, opProd1, myProd2, opProd2, opProd3;
		// obtain production quantities for each agent
		myProd1 = getProduction(1); myProd2 = getProduction(2);
		opProd1 = ics.getOtherProduction(this, 1);
		opProd2 = ics.getOtherProduction(this, 2);
		opProd3 = ics.getOtherProduction(this, 3);
		// production comparison for first step
		if (myProd1 > opProd1) {
			oppEqualProduction1 = false;
			oppGreaterProduction1 = false;
		} else if (myProd1 < opProd1) {
			oppEqualProduction1 = false;
			oppGreaterProduction1 = true;
		} else {
			oppEqualProduction1 = true;
			oppGreaterProduction1 = false;
		}
		// production comparison for second step
		if (myProd2 > opProd2) {
			oppEqualProduction2 = false;
			oppGreaterProduction2 = false;
		} else if (myProd2 < opProd2) {
			oppEqualProduction2 = false;
			oppGreaterProduction2 = true;
		} else {
			oppEqualProduction2 = true;
			oppGreaterProduction2 = false;
		}

		// did opponent raise or lower production
		// first step
		if (opProd1 > opProd2) {
			oppRaisedProduction1 = true;
			oppMaintainedProduction1 = false;
		} else if (opProd1 < opProd2) {
			oppRaisedProduction1 = false;
			oppMaintainedProduction1 = false;
		} else {
			oppMaintainedProduction1 = true;
			oppRaisedProduction1 = false;
		}
		// second step
		if (opProd2 > opProd3) {
			oppRaisedProduction2 = true;
			oppMaintainedProduction2 = false;
		} else if (opProd2 < opProd3) {
			oppRaisedProduction2 = false;
			oppMaintainedProduction2 = false;
		} else {
			oppMaintainedProduction2 = true;
			oppRaisedProduction2 = false;
		}
		
		// did my revenue change
		double myRev1 = revenue[shortIndex(1)];
		double myRev2 = revenue[shortIndex(2)];
		double myRev3 = revenue[shortIndex(3)];
		// first step
		if (myRev1 > myRev2) {
			myRevenueIncreased1 = true;
			myRevenueUnchanged1 = false;
		} if (myRev1 < myRev2) {
			myRevenueIncreased1 = false;
			myRevenueUnchanged1 = false;
		} else {
			myRevenueUnchanged1 = true;
			myRevenueIncreased1 = false;
		}
		// second step
		if (myRev2 > myRev3) {
			myRevenueIncreased2 = true;
			myRevenueUnchanged2 = false;
		} if (myRev2 < myRev3) {
			myRevenueIncreased2 = false;
			myRevenueUnchanged2 = false;
		} else {
			myRevenueUnchanged2 = true;
			myRevenueIncreased2 = false;
		}
			
		int strategyIndex = 0x0;
		
		if (oppEqualProduction1)
			strategyIndex += oep1;
		if (oppEqualProduction2)
			strategyIndex += oep2;
		if (oppGreaterProduction1)
			strategyIndex += ogp1;
		if (oppGreaterProduction2)
			strategyIndex += ogp2;
		if (oppMaintainedProduction1)
			strategyIndex += omp1;
		if (oppMaintainedProduction2)
			strategyIndex += omp2;
		if (oppRaisedProduction1)
			strategyIndex += orp1;
		if (oppRaisedProduction2)
			strategyIndex += orp2;
		if (myRevenueUnchanged1)
			strategyIndex += mru1;
		if (myRevenueUnchanged2)
			strategyIndex += mru2;
		if (myRevenueIncreased1)
			strategyIndex += mri1;
		if (myRevenueIncreased2)
			strategyIndex += mri2;
		
		boolean changeProduction = ind.genome[strategyIndex];
		boolean raiseProduction = ind.genome[strategyIndex +1];
		
		// finally, execute GA determined strategy
		if (changeProduction) {
			if (raiseProduction) {
				raiseProduction();
			} else {
				lowerProduction();
			}
		}
	}	
		
		
	private void raiseProduction() {
		this.production[shortIndex()] = this.production[shortIndex(1)] + productionInterval;
	}
		

	private void lowerProduction() {
		this.production[shortIndex()] = this.production[shortIndex(1)] - productionInterval;
	}

	@Override
	public double getProduction(int stepsAgo) {
		return production[shortIndex(stepsAgo)];
	}

	@Override
	public void earnRevenue(double revenue) {
		this.revenue[shortIndex()] = revenue;
		this.totalRevenue += revenue;
	}
	
	@Override
	public Fitness getFitness() {
		return ind.fitness;
	}
	
	@Override
	public double getTotalRevenue() {
		return totalRevenue;
	}

	@Override
	public Individual getIndividual() {
		return ind;
	}
	
	

}
