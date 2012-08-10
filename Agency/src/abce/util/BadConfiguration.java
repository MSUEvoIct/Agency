package abce.util;


public class BadConfiguration extends Exception {

	private static final long	serialVersionUID	= 1L;

	private final StringBuffer	_msg				= new StringBuffer();
	private final static String	endl				= System.getProperty("line.separator");



	public BadConfiguration() {
	}



	public BadConfiguration(String msg) {
		_msg.append(msg);
	}



	public void prepend(String premsg) {
		_msg.insert(0, premsg + endl);
	}



	public void append(String msg) {
		if (_msg.length() != 0)
			_msg.append(endl);
		_msg.append(msg);
	}



	public void validate() throws BadConfiguration {
		if (_msg.length() > 0) {

			throw this;
		}
	}



	@Override
	public String getMessage() {
		return _msg.toString();
	}
}
