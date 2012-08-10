package abce.util.io;

public interface FormattedBuffer {
	public static final String endl = System.getProperty("line.separator");
	public String toString();
	public StringBuffer getStringBuffer();
	public StringBuffer buf();
}
