package abce.test.agency.ec.ecj;

import static org.junit.Assert.*;

import org.junit.Test;

import ec.agency.gp.terminals.FalseGP;
import ec.agency.gp.terminals.TrueGP;
import ec.agency.gp.types.BooleanGP;


public class TestBooleanGP {

	@Test
	public void test() {
		FalseGP bool_f = new FalseGP();
		TrueGP bool_t = new TrueGP();
		
		BooleanGP input = new BooleanGP();
		
		bool_f.eval(null, 0, input, null, null, null);
		assertEquals(input.value, false);
		
		bool_t.eval(null, 0, input, null, null, null);
		assertEquals(input.value, true);
	}

}
