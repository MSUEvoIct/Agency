package ec.agency.iteratedcournot;

import ec.vector.FloatVectorIndividual;

public class ICAEquation extends FloatVectorIndividual implements IteratedCournotAgent {
	private static final long serialVersionUID = 1L;
	
	private static enum p {
		AssumeMyProdTm1,
		AssumeMyProdTm2,
		AssumeOthProdTm1,
		AssumeOthProdTm2,
		AssumePriceTm1,
		AssumePriceTm2,
		Constant,
		CoefMyProd1,
		CoefMyProd2,
		CoefOthProd1,
		CoefOthProd2,
		CoefPrice1,
		CoefPrice2
	}

	@Override
	public float getProduction(ProductionStimulus prodStim) {
		// Actualize assumptions in the first two steps
		if (prodStim.step == 0) {
			prodStim.myLastProduction = genome[p.AssumeMyProdTm1.ordinal()];
			prodStim.myLastProduction2 = genome[p.AssumeMyProdTm2.ordinal()];
			prodStim.othersLastProduction = genome[p.AssumeOthProdTm1.ordinal()];
			prodStim.othersLastProduction2 = genome[p.AssumeOthProdTm2.ordinal()];
			prodStim.price = genome[p.AssumePriceTm1.ordinal()];
			prodStim.price2 = genome[p.AssumePriceTm2.ordinal()];
		} else if (prodStim.step == 1) {
			prodStim.myLastProduction2 = genome[p.AssumeMyProdTm1.ordinal()];
			prodStim.othersLastProduction2 = genome[p.AssumeOthProdTm1.ordinal()];
			prodStim.price2 = genome[p.AssumePriceTm1.ordinal()];
		}

		// Price is determined by a linear equation with coefficients specified
		// by the genome
		float price = 0;
		
		price += genome[p.Constant.ordinal()];
		price += genome[p.CoefMyProd1.ordinal()] * prodStim.myLastProduction;
		price += genome[p.CoefMyProd2.ordinal()] * prodStim.myLastProduction2;
		price += genome[p.CoefOthProd1.ordinal()] * prodStim.othersLastProduction;
		price += genome[p.CoefOthProd2.ordinal()] * prodStim.othersLastProduction2;
		price += genome[p.CoefPrice1.ordinal()] * prodStim.price;
		price += genome[p.CoefPrice2.ordinal()] * prodStim.price2; 
		
		return price;
	
	}

}
