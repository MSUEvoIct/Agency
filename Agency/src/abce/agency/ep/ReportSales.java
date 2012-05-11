package abce.agency.ep;


import abce.agency.consumer.*;
import abce.agency.engine.*;
import abce.agency.goods.*;
import evoict.*;
import evoict.ep.*;
import evoict.io.*;



public class ReportSales implements Procedure {

	private static final long	serialVersionUID	= 1L;
	boolean						untriggered			= true;
	String						prefix;
	DelimitedOutFile			out;
	final String				format				= "Generation%d,SimulationID%d,Step%f,ConsumerID%d,GoodID%d,PastQuantity%0.2f,PastPaid%0.2f";



	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
		prefix = (args.containsKey("prefix")) ? args.get("prefix") : "sales";

	}



	@Override
	public void process(Object... context) throws Exception {
		MarketSimulation sim = (MarketSimulation) context[0];
		String dir = sim.simulationRoot.getPath();
		if (untriggered) {
			untriggered = false;
			String path = dir + "/" + prefix + "-" + sim.generation + "-" + sim.simulationID + ".csv.gz";
			out = new DelimitedOutFile(path, format);
		}
		for (Consumer c : sim.getConsumers()) {
			for (Good g : c.allDesiredGoods()) {
				out.write(sim.generation, sim.simulationID, c.agentID, g.id, c.getPastQty(g, 0), c.getPastPaid(g, 0));
			}
		}
	}



	@Override
	public void finish() {
		out.close();

	}

}
