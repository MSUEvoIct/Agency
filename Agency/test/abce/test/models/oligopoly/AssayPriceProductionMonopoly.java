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
		

		double[] endowments = {1000,10000,100000,1000000};
		double[] prodcosts = {10,20,30,40,50,60,70,80,90,100,120};
		
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
		
		for (double endowment : endowments){
			for (double prodcost: prodcosts){
				for (int step: steps){
					for (double price : prices){
						for (double prod : production){
							System.err.println("step:" + step + "  price:" + price + "  prod:" + prod);
							OligopolySimulation sim = new OligopolySimulation(0, CONFIG_PATH, -1);
							
							/**
							 * Configuration has to be set via the "setProperty()" method; the initialize
							 * method at the end of block translates these properties into the the indiv
							 * class members.
							 */
							sim.getConfig().setProperty("firm_endowment", Double.toString(endowment));
							sim.getConfig().setProperty("cost_constant", Double.toString(prodcost));
							sim.getConfig().setProperty("steps_to_run", Integer.toString(step));
							sim.getConfig().setProperty("firm_initial_price", Double.toString(price));
							sim.getConfig().setProperty("firm_initial_production", Double.toString(prod));
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
			}
		}
		fot.close();
	}

}
