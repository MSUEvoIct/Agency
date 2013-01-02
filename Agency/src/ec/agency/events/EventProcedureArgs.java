package ec.agency.events;


import java.util.HashMap;



public class EventProcedureArgs extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;



	public EventProcedureArgs() {
		super();
	}



	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (String s : keySet()) {
			buf.append(s + "=" + get(s) + " ");
		}
		return buf.toString();
	}
}
