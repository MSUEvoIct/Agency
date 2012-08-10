package abce.agency.events;

import java.util.LinkedHashMap;

import abce.util.BadConfiguration;
import abce.util.events.EventProcedure;
import abce.util.events.EventProcedureContainer;
import abce.util.events.EventProcedureDescription;
import abce.util.events.EventProcedureManager;
import abce.util.events.ValuedEventProcedureQueue;






public class MSEventProcedureManager extends EventProcedureManager {

	/**
	 * 
	 */
	private static final long								serialVersionUID	= 1L;

	public final static byte								EVENT_STEP			= 0;

	protected LinkedHashMap<Byte, EventProcedureContainer>	events				= new LinkedHashMap<Byte, EventProcedureContainer>();



	public MSEventProcedureManager() {
		events.put(EVENT_STEP, new ValuedEventProcedureQueue());
	}



	@Override
	public void addEvent(EventProcedureDescription desc) throws BadConfiguration {
		String type = desc.getEventType().trim().toUpperCase();
		if (type.equals("STEP")) {
			MSValuedEventProcedure vep = new MSValuedEventProcedure(EVENT_STEP, desc);
			events.get(EVENT_STEP).add(vep);
		}
	}



	@Override
	public void process(byte event_id, Object context, Object... proc_context) {
		if (events.containsKey(event_id)) {
			EventProcedure[] triggered = events.get(event_id).processContext(context);
			for (EventProcedure ep : triggered) {
				try {
					ep.execute(proc_context);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
	}



	@Override
	public void finish() {
		for (EventProcedureContainer epc : events.values()) {
			epc.finish();
		}

	}

}
