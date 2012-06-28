package abce.agency.events;


import abce.agency.MarketSimulation;
import abce.agency.util.BadConfiguration;



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
