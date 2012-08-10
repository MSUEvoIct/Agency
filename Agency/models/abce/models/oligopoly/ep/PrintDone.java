package abce.models.oligopoly.ep;


import abce.agency.MarketSimulation;
import abce.util.events.EventProcedureArgs;
import abce.util.events.Procedure;
import abce.util.BadConfiguration;



public class PrintDone implements Procedure {

	@Override
	public void setup(EventProcedureArgs args) throws BadConfiguration {
		// TODO Auto-generated method stub

	}



	@Override
	public void process(Object... context) throws Exception {
		MarketSimulation sim = (MarketSimulation) context[0];
		System.err.println("Model done: " + sim.simulationID + " @ time " + sim.schedule.getTime());

	}



	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
