package abce.test.util;


import org.junit.Test;

import abce.ecj.ep.EPSimpleEvolutionState;



/**
 * This is just a quick test to see if we can create a new instance of
 * EPSimpleEvolutionState(). Sometimes ECJ will throw a fatal error during
 * construction but not dump the stack
 * 
 * @author ruppmatt
 * 
 */
public class TestInstantiateEPSES {

	@Test
	public void test() {
		EPSimpleEvolutionState state = new EPSimpleEvolutionState();
	}

}
