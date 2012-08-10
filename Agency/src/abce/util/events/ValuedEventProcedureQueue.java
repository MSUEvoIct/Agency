package abce.util.events;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;




/**
 * A ValuedEventProcedureQueue holds a set of ValueEventProcedures and provides
 * the interface to an EentProcedureContainer.
 * 
 * @author ruppmatt
 * 
 */
public class ValuedEventProcedureQueue implements EventProcedureContainer {

	PriorityQueue<ValuedEventProcedure>	_ea	= new PriorityQueue<ValuedEventProcedure>(1, new ValueEventComparator());



	/**
	 * Construct an empty queue
	 */
	public ValuedEventProcedureQueue() {
	}



	@Override
	/**
	 * Add an event procedure
	 * @param ea
	 * 	A valued event procedure
	 * @return
	 * 
	 */
	public void add(EventProcedure ea) {
		if (!(ea instanceof ValuedEventProcedure)) {
			return;
		}
		_ea.offer((ValuedEventProcedure) ea);

	}



	/**
	 * Given an object (should be a double) process the events in the queue that
	 * occurred after and up to this point.
	 * 
	 * @param context
	 *            The context (should be a double) to process
	 * @return
	 *         The event procedures to execute.
	 */
	@Override
	public EventProcedure[] processContext(Object context) {
		ArrayList<EventProcedure> to_trigger = new ArrayList<EventProcedure>();
		if (_ea.size() > 0) {
			EventContext next_context = _ea.element().examine(context);
			while (next_context.equals(EventContext.PAST)
					|| next_context.equals(EventContext.CURRENT)) {
				ValuedEventProcedure vea = _ea.poll();
				to_trigger.add(vea);
				vea.increment();
				if (!vea.finished() && !vea.isEnd(context)) {
					_ea.offer(vea);
				}
				next_context = (_ea.isEmpty()) ? EventContext.UNKNOWN : _ea.element().examine(context);
			}
		}
		EventProcedure[] to_return = to_trigger.toArray(new EventProcedure[to_trigger.size()]);
		return to_return;
	}



	/**
	 * See what event could be triggered next, but don't update them.
	 * 
	 * @param context
	 *            The context (should be a Double) to process
	 * @return
	 *         The event procedures that are triggered under that context
	 */
	@Override
	public EventProcedure[] peekContext(Object context) {
		ArrayList<EventProcedure> to_trigger = new ArrayList<EventProcedure>();
		if (_ea.size() > 0) {
			ValuedEventProcedure[] queue_shadow = _ea.toArray(new ValuedEventProcedure[_ea.size()]);
			for (int k = 0; k < queue_shadow.length; k++) {
				EventContext element_context = queue_shadow[k].examine(context);
				if (element_context.equals(EventContext.PAST) || element_context.equals(EventContext.CURRENT)) {
					to_trigger.add(queue_shadow[k]);
				} else if (element_context.equals(EventContext.FUTURE)) {
					break;
				}
			}
		}
		return to_trigger.toArray(new EventProcedure[to_trigger.size()]);
	}



	/**
	 * Return the number of events that can be triggered with a given context
	 * 
	 * @param context
	 *            The event context (should be a Double)
	 * @return
	 *         The number of event procedurs triggered by the contxt
	 */
	@Override
	public int numWithContext(Object context) {
		Double current = (Double) context;
		int count = 0;
		ValuedEventProcedure[] queue_shadow = _ea.toArray(new ValuedEventProcedure[_ea.size()]);
		for (int k = 0; k < queue_shadow.length; k++) {
			if (queue_shadow[k].next() < current) {
				count++;
			} else if (queue_shadow[k].next() > current) {
				break;
			}
		}
		return count;
	}



	/**
	 * Return all event contexts in the queue.
	 * 
	 * @return
	 *         The event contexts in the queue
	 */
	@Override
	public EventProcedure[] get() {
		return _ea.toArray(new EventProcedure[_ea.size()]);
	}



	/**
	 * Return the number of event procedures in the container
	 * 
	 * @return
	 *         The number of event procedures in the container
	 */
	@Override
	public int size() {
		return _ea.size();
	}



	@Override
	public void finish() {
		for (ValuedEventProcedure vep : _ea.toArray(new ValuedEventProcedure[_ea.size()])) {
			Procedure proc = vep.getProcedure();
			if (proc != null) {
				proc.finish();
			}
		}
		_ea.clear();
	}
}



/**
 * This class provides a way to sort value event procedures
 * 
 * @author ruppmatt
 * 
 */

class ValueEventComparator implements Comparator<ValuedEventProcedure>, Serializable {

	private static final long	serialVersionUID	= 1L;



	@Override
	public int compare(ValuedEventProcedure lhs, ValuedEventProcedure rhs) {
		if (lhs.next() > rhs.next())
			return 1;
		else if (lhs.next() < rhs.next())
			return -1;
		else
			return 0;
	}
}