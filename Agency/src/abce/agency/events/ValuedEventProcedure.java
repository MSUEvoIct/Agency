package abce.agency.events;


import abce.agency.util.BadConfiguration;



public abstract class ValuedEventProcedure extends EventProcedure {

	private static final long	serialVersionUID	= 1L;
	protected Interval			_interval;



	/**
	 * Value Event Actions are event actions that are triggered on or after a
	 * particular value is reached.
	 * 
	 * @param type
	 *            The byte ID of the event
	 * @param desc
	 *            The event description to construct the action from
	 * @throws BadConfiguration
	 */
	public ValuedEventProcedure(byte type, EventProcedureDescription desc) throws BadConfiguration {
		super(type, desc);
		String[] ival_tok = desc.getEventValue().split("\\s+", 2);
		_interval = new Interval(ival_tok[0]);
	}



	/**
	 * What is the first triggered value of the event?
	 * 
	 * @return
	 */
	public double start() {
		return _interval.start();
	}



	/**
	 * How often is the even triggered?
	 * 
	 * @return
	 */
	public double interval() {
		return _interval.interval();
	}



	public boolean isEnd(Object context) {
		Double value = (Double) context;
		return value.equals(Interval.ATEND);
	}



	/**
	 * What is the last value the event triggers?
	 * 
	 * @return
	 */
	public double finish() {
		return _interval.finish();
	}



	/**
	 * What value will the event trigger next?
	 * 
	 * @return
	 */
	public double next() {
		return _interval.next();
	}



	/**
	 * Increment the trigger value
	 */
	public void increment() {
		_interval.increment();
		if (_interval.completed()) {
			_completed = true;
		}
	}



	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("ValueEvent( " + _interval.toString() + " " + super.toString() + ")");
		return buf.toString();
	}



	@Override
	public EventContext examine(Object context) {
		Double value = (Double) context;
		Double next = _interval.next();
		if (value.equals(Interval.ATEND) && _interval.finish() == Interval.ATEND) {
			return EventContext.CURRENT;
		} else if (next < value) {
			return EventContext.PAST;
		} else if (next.equals(value)) {
			return EventContext.CURRENT;
		} else {
			return EventContext.FUTURE;
		}
	}

}
