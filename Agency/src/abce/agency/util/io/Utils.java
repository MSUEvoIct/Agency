package abce.agency.util.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {
	
	private Utils(){
	}
	
	public static String rep(char c, int len){
		StringBuffer buf = new StringBuffer();
		for (int k=0; k<len; k++)
			buf.append(c);
		return buf.toString();
	}
	
	public static String rep(String s, int len){
		StringBuffer buf = new StringBuffer();
		for (int k=0; k<len; k++)
			buf.append(s);
		return buf.toString();
	}
	
	public static Collection<String> getWords(String s){
		String[] tokens = s.split("\\s+");
		ArrayList<String> words = new ArrayList<String>();
		
		//Separate \\n markup to force a line break
		for (String t : tokens){
			words.addAll(splitNewLines(t));
		}
		
		return words;
	}
	
	public static Collection<String> splitNewLines(String s){
		ArrayList<String> words = new ArrayList<String>();
		Pattern newline = Pattern.compile(",,");
		Matcher m = newline.matcher(s);
		int start_ndx = 0;
		while (m.find()){ 	
			int begin_p = m.start();
			int end_p   = m.end();
			if (start_ndx != begin_p){ //There is a substring before the newline
				words.add( s.substring(start_ndx,begin_p) );
			}
			words.add( s.substring(begin_p,end_p) );
			start_ndx = end_p;
		}
		if (start_ndx < s.length()){ //There is a substring after the last newline
			words.add( s.substring(start_ndx) );
		}
		return words;
	}
	
	public static Boolean isNewLine(String s){
		return s.equals(",,");
	}
	
	public static String formatElapsedTime(long t){
		int milsec = 1000;
		int milmin = milsec*60;
		int milhour = milmin*60;
		int milday = milhour*24;
		long days = t / milday;
		t = t % milday;
		long hours = t / milhour;
		t = t % milhour;
		long mins  = t/milmin;
		t = t % milmin;
		long sec = t/milsec;
		long ms = t%milsec;
		return String.format("%d:%02d:%02d:%02d.%d", days,hours,mins,sec,ms);
	}

}
