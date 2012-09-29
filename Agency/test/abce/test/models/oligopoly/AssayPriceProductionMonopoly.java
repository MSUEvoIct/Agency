package abce.test.models.oligopoly;

import java.io.IOException;
import java.util.ArrayList;

import abce.agency.firm.Firm;
import abce.agency.firm.FixedProductionPricingFirm;
import abce.models.oligopoly.OligopolySimulation;
import abce.util.io.DelimitedOutFile;

public class AssayPriceProductionMonopoly {
	
	private static final String CONFIG_PATH = "etc/oligopoly/assay-priceprod/oligopoly.cfg";
	public static final String OUT_FMT = "SimulationID%d,FirmID%d,Steps%d,Price%f,Production%f,Networth%f";
	
	public static void main(String[] args){
		

		int[] steps = {1, 2, 8, 64};
		
		ArrayList<Double> prices = new ArrayList<Double>();
		for (double price=5.0; price <= 120; price+=5){
			prices.add(price);
		}
		
		ArrayList<Double> production = new ArrayList<Double>();
		for (double prod=0; prod <= Math.pow(10, 5); prod+=500){
			production.add(prod);
		}
		
		DelimitedOutFile fot = null;
		try {
			fot = new DelimitedOutFile("assay_priceprod_monopoly.csv.gz", OUT_FMT);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		for (int step: steps){
			for (double price : prices){
				for (double prod : production){
					System.err.println("step:" + step + "  price:" + price + "  prod:" + prod);
					OligopolySimulation sim = new OligopolySimulation(0, CONFIG_PATH, -1);
					sim.getConfig().steps_to_run = step;
					sim.initialize();
					FixedProductionPricingFirm firm = new FixedProductionPricingFirm(price, prod);
					sim.setupFirm(firm);
					try {
						sim.call();
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(1);
					}
					for (Firm f : sim.getFirms()){
						fot.write(
								sim.simulationID,
								f.agentID,
								step,
								price,
								prod,
								f.getAccounts().getNetWorth()
								);
					}
					
				}
			}
		}
		fot.close();
	}

}
