package ec.agency.util;


public class PathResolver {

	/**
	 * Return a prefix of a path or null if not present
	 * 
	 * @param pathkey
	 * @return
	 */
	public static String getPrefix(String pathkey) {
		int ndx = pathkey.indexOf('.');
		return (ndx < 0) ? null : pathkey.substring(0, ndx);
	}



	/**
	 * Return a prefix-free path
	 * 
	 * @param pathkey
	 * @return
	 */
	public static String extractPrefix(String pathkey) {
		int ndx = pathkey.indexOf('.');
		return (ndx < 0) ? pathkey : pathkey.substring(ndx + 1);
	}



	/**
	 * Return the final name of the resolution
	 * 
	 * @param pathkey
	 * @return
	 */
	public static String getName(String pathkey) {
		int ndx = pathkey.lastIndexOf('.');
		return (ndx < 0) ? pathkey : pathkey.substring(ndx + 1);

	}
}
