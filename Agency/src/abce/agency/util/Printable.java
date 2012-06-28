package abce.agency.util;

/**
 * 
 * @author ruppmatt
 *		Printables have a toString method and OS agnostic constants.
 */
public interface Printable {
	public static final String ENDL = System.getProperty("line.separator");
	public String toString();
	
}
