package abce.agency.util;

public class StringUtil {
	
	public static String repeat(String item, int num){
		StringBuffer buf = new StringBuffer();
		for (int k = 0; k < num; k++){
			buf.append(item);
		}
		return buf.toString();
	}
	
	public static void repeat(StringBuffer buf, String item, int num){
		for (int k = 0; k < num; k++){
			buf.append(item);
		}
	}

}
