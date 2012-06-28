package abce.agency.test.events;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import abce.agency.events.ContextFreeEventProcedure;
import abce.agency.events.EventContext;
import abce.agency.events.EventProcedure;
import abce.agency.events.EventProcedureDescription;
import abce.agency.events.ValuedEventProcedureQueue;
import abce.agency.util.BadConfiguration;



/**
 * This test tries to create, examine, and test EventProcedures
 * 
 * @author ruppmatt
 * 
 */
public class TestEventProcedureDescriptions {

	@Test
	public void test() {

		// A simple event with a context-free event procedure; the procedure
		// modifies the state of the procedure context
		String descA = "EVENT ~ evoict.test.ep.TestProcedureStoreArgument to_store=received";
		try {
			EventProcedureDescription epd_descA = new EventProcedureDescription(descA);

			// Test the individual components of the description
			assertEquals("EVENT", epd_descA.getEventType());
			assertEquals("", epd_descA.getEventValue());
			assertEquals(TestProcedureStoreArgument.class, epd_descA.getProcedureClass());
			assertEquals("to_store=received", epd_descA.getProcedureArguments().toString().trim());

			// Create an EventProcedure based on the event procedure description
			ContextFreeEventProcedure cfp = new ContextFreeEventProcedure((byte) 0, epd_descA);

			// The test context gets modified when the procedure executes
			ProcedureTestContext context = new ProcedureTestContext();

			// Execute the event procedure
			try {
				cfp.execute(context);
			} catch (Exception e) {
				fail();
			}

			// The context should have been modified by the procedure
			assertEquals("received", context.received_argument);

		} catch (BadConfiguration e) {
			System.err.println(e.getMessage());
			fail();
		}

		String descB = "VALUE_EVENT 0:10:end ~ evoict.test.ep.TestProcedureCounter";
		try {
			ValuedEventProcedureQueue q = new ValuedEventProcedureQueue();

			EventProcedureDescription epd_descB = new EventProcedureDescription(descB);
			assertEquals("VALUE_EVENT", epd_descB.getEventType());
			assertEquals("0:10:end", epd_descB.getEventValue());
			assertEquals(TestProcedureCounter.class, epd_descB.getProcedureClass());
			assertEquals(0, epd_descB.getProcedureArguments().size());
			TestValuedEventProcedure tvep = new TestValuedEventProcedure((byte) 0, epd_descB);

			// Add the event procedure to our queue; it should be accecpted as
			// the only member
			q.add(tvep);
			assertEquals(1, q.size());

			for (int k = 0; k <= 100; k++) {

				// The event should either take place now or in the future
				if (k % 10 == 0) {
					assertEquals(EventContext.CURRENT, tvep.examine((double) k));
				} else {
					assertEquals(EventContext.FUTURE, tvep.examine((double) k));
				}

				// Peeking at what events will get triggered should not
				// increment their counters
				EventProcedure[] eps = q.peekContext((double) k);
				if (k % 10 == 0) {
					assertEquals(1, eps.length);
					assertEquals(EventContext.CURRENT, eps[0].examine((double) k));
				} else {
					assertEquals(0, eps.length);
				}

				// Processing the event will increment the counters
				eps = q.processContext((double) k);
				assertEquals(EventContext.FUTURE, tvep.examine((double) k));

				if (k % 10 == 0) {
					assertEquals(1, eps.length);
				} else {
					assertEquals(0, eps.length);
				}
				for (EventProcedure ep : eps) {
					try {
						ep.execute((Object[]) null);
					} catch (Exception e) {
						fail();
					}
				}
			}

			// The event should have been triggered 11 times
			assertEquals(11, ((TestProcedureCounter) tvep.getProcedure()).counter);

		} catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
	}
}
