package ec.agency.io;


import java.io.Serializable;



public class Notifier implements Serializable {

	private static final long	serialVersionUID	= 1L;
	public Boolean				_terminating		= false;



	public Notifier() {
	}

	public final String	endl	= System.getProperty("line.separator");



	public void debug(String s) {
		System.err.println("Debug: " + s);
	}



	public void fatal(String s) {
		fatal(s, ErrorCode.Unknown);
	}



	public void fatal(String s, ErrorCode c) {
		System.err.println("Fatal error: " + s);
		System.err.println("[Code " + c.code().toString() + "]");
	}



	public void warn(String s) {
		System.err.println("WARNING: " + s);
	}



	public void notify(String s) {
		System.out.println(s);
	}

}
