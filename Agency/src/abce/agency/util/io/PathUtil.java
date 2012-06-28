package abce.agency.util.io;


public class PathUtil {

	public static boolean makeDirectory(String path) {
		java.io.File dir = new java.io.File(path);
		if (!dir.exists()) {
			return dir.mkdir();
		} else {
			return dir.isDirectory();
		}
	}

}
