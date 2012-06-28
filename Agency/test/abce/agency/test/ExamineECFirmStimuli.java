package abce.agency.test;


import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import abce.agency.firm.ECProdPriceFirm;
import abce.agency.reflection.StimulusManager;
import abce.agency.util.io.GZOutFile;



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
		examine(ECProdPriceFirm.class, "ECFirm-Stimuli.csv.gz");

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
