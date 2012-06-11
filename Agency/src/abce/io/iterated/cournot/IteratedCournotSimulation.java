package abce.io.iterated.cournot;

import sim.engine.SimState;
import abce.ecj.FileManager;
import evoict.io.DelimitedOutFile;

public class IteratedCournotSimulation extends SimState {
	private static final long serialVersionUID = 1L;
	
	public static FileManager fm = new FileManager();
	static {
		fm = new FileManager();
		try {
			fm.initialize(".");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public final String			productionFormat				= "Generation%d,SimulationID%d,Step%f,Agent%d,Production%f,Price%f";
	public final String	productionFile = "production.csv.gz";

	
	IteratedCournotAgent first, second;
	public int simulationID;
	public int generation;

	public IteratedCournotSimulation(long seed, IteratedCournotAgent first, IteratedCournotAgent second) {
		super(seed);
		this.first = first;
		this.second = second;
		this.schedule.scheduleRepeating(first);
	}

	public double getOtherProduction(IteratedCournotAgent requesting, int stepsAgo) {
		if (stepsAgo <= 0)
			throw new RuntimeException("Quantities are determined simultaneously in Cournot");
		if (requesting == first)
			return second.getProduction(stepsAgo);
		if (requesting == second)
			return first.getProduction(stepsAgo);
		else
			throw new RuntimeException("getOtherProduction() must be called by one of the two agents.");
	}

	public void run(int steps) {
		for(int i = 0; i < steps; i++){
			// Let agents step() and make decisions
			schedule.step(this);
			
			// determine market price / do market clearing
			double firstProduction = first.getProduction(0);
			double secondProduction = second.getProduction(0);
			double totalProduction = firstProduction + secondProduction; 
			double price = getPrice(totalProduction);
			
			first.earnRevenue(firstProduction * price);
			second.earnRevenue(firstProduction * price);
			if ((generation % 10 == 0) && (simulationID %10 == 0) && (schedule.getTime() > 48)) {
				try {
					DelimitedOutFile production = fm.getDelimitedOutFile(productionFile, productionFormat);
					production.write(generation,simulationID,schedule.getTime(), 0, first.getProduction(0), price);
					production.write(generation,simulationID,schedule.getTime(), 1, second.getProduction(0), price);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			
		}
	}
	
	public double getPrice(double productionQty) {
		return 100 - productionQty;
	}
	
	
	
}
