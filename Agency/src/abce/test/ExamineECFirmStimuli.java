package abce.test;


import java.io.*;

import org.junit.*;

import abce.agency.firm.*;
import evoict.io.*;
import evoict.reflection.*;



/**
 * This test simply examines one StimulusResponse class for all available paths,
 * outputting the web in a compressed CSV file.
 * 
 * @author ruppmatt
 * 
 */
public class ExamineECFirmStimuli {

	@Test
	public void test() {
		examine(ECFirm.class, "ECFirm-Stimuli.csv.gz");

	}



	public void examine(Class<?> cl, String path) {
		GZOutFile fot;
		try {
			fot = new GZOutFile(path);
			StimulusManager manager = new StimulusManager();
			manager.scanClass(cl);
			String s = manager.writeDescriptionWeb(cl);
			fot.write(s);
			fot.close();
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}

	}
}
