package abce.test.util.reflection;

import static org.junit.Assert.*;

import org.junit.Test;

import ec.agency.StimulusResponse;
import ec.agency.reflection.RestrictedMethodDictionary;

import abce.agency.firm.sr.ScaleFirmPriceSR;

public class DisplaySRDictionaryPaths {

	@Test
	public void test() {
		StimulusResponse sr = new ScaleFirmPriceSR();
		for (String path : ( (RestrictedMethodDictionary) sr.dictionary() ).enumerate()){
			System.err.println(path);
		}
	}

}
