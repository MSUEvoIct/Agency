package abce.models.oligopoly.ep;


import abce.agency.consumer.Consumer;
import abce.agency.goods.Good;
import abce.util.io.DelimitedOutFile;
import abce.util.events.EventProcedureArgs;
import abce.models.oligopoly.OligopolySimulation;
import abce.util.BadConfiguration;
import abce.util.events.Procedure;



public class ReportSales implements Procedure {

	private static final long	serialVersionUID	= 1L;
	public String				name;
	public final String			format				= "Generation%d,SimulationID%d,Step%f,ConsumerID%d,GoodID%d,PastQuantity%.2f,PastPaid%.2f";



	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
		name = (args.containsKey("name")) ? args.get("name") : "sales";
	}



	@Override
	public void process(Object... context) throws Exception {
		OligopolySimulation sim = (OligopolySimulation) context[0];
		String path = name + ".csv.gz";
		DelimitedOutFile out = sim.file_manager.getDelimitedOutFile(path, format);

		for (Consumer c : sim.getConsumers()) {
			for (Good g : c.allDesiredGoods()) {
				out.write(sim.generation, sim.simulationID, sim.schedule.getTime(), c.agentID, g.id,
						c.getPastQty(g, 0), c.getPastPaid(g, 0));
			}
		}
	}



	@Override
	public void finish() {
	}
}
