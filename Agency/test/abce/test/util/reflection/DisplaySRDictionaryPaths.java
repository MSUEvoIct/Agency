package abce.test.util.reflection;

import static org.junit.Assert.*;

import org.junit.Test;

import abce.agency.ec.StimulusResponse;
import abce.agency.firm.sr.ScaleFirmPriceSR;
import abce.util.reflection.RestrictedMethodDictionary;

public class DisplaySRDictionaryPaths {

	@Test
	public void test() {
		StimulusResponse sr = new ScaleFirmPriceSR();
		for (String path : ( (RestrictedMethodDictionary) sr.dictionary() ).enumerate()){
			System.err.println(path);
		}
	}

}
