package test;


import org.junit.*;

import abce.agency.firm.sr.*;
import evoict.io.*;
import evoict.reflection.*;



/**
 * This test simply examines one StimulusResponse class for all available paths,
 * outputting the web in a compressed CSV file.
 * 
 * @author ruppmatt
 * 
 */
public class ExamineECJSimpleFirmSR {

	@Test
	public void test() {
		examine(ECJSimpleFirmPriceSR.class, "ECJSimpleFirmPriceSR.csv.gz");
	}



	public void examine(Class<?> cl, String path) {
		TextOutFile fot = new TextOutFile(null, path);
		StimulusManager manager = new StimulusManager();
		manager.scanClass(cl);
		String s = manager.writeDescriptionWeb(cl);
		fot.write(s);
		fot.close();
	}
}
