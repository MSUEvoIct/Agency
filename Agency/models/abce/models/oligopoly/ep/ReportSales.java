package abce.models.oligopoly.ep;


import ec.agency.consumer.Consumer;
import ec.agency.events.EventProcedureArgs;
import ec.agency.events.Procedure;
import ec.agency.goods.Good;
import ec.agency.io.DelimitedOutFile;
import ec.agency.util.BadConfiguration;
import abce.models.oligopoly.OligopolySimulation;



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
