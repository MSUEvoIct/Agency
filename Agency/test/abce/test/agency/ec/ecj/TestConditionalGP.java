package abce.test.agency.ec.ecj;

import static org.junit.Assert.*;

import org.junit.Test;

import ec.agency.gp.operators.DoubleSelector;
import ec.agency.gp.operators.GreaterThanGP;
import ec.agency.gp.operators.LessThanGP;
import ec.agency.gp.types.BooleanGP;
import ec.agency.gp.types.DoubleGP;
import ec.gp.GPNode;


public class TestConditionalGP {

	@Test
	public void test() {
		testDoubleSelector();
		testLessThanGP();
		testGreaterThanGP();
	}
	
	
	public void biOpTest(GPNode n, double[][] operands, boolean[] expected){
		n.children = new GPNode[2];
		for (int k=0; k<expected.length; k++){
			n.children[0] = new ConstDoubleNode(operands[k][0]);
			n.children[1] = new ConstDoubleNode(operands[k][1]);
			BooleanGP input = new BooleanGP();
			n.eval(null, 0, input, null, null, null);
			assertTrue(input.value == expected[k]);
		}
	}
	
	
	public void testDoubleSelector(){
		boolean[] op_sel = {true, false, true};
		double[][] op_pairs = { {20,30}, {40,50}, {60, 70} };
		double[] expected = {20,50,60};
		DoubleSelector n = new DoubleSelector();
		n.children = new GPNode[3];
		for (int k=0; k<expected.length; k++){
			DoubleGP input = new DoubleGP();
			n.children[0] = new ConstBooleanNode(op_sel[k]);
			n.children[1] = new ConstDoubleNode(op_pairs[k][0]);
			n.children[2] = new ConstDoubleNode(op_pairs[k][1]);
			n.eval(null, 0, input, null, null, null);
			assertEquals(expected[k], input.value, 0.001);
		}
		
		
	}
	
	public void testLessThanGP(){
		LessThanGP n = new LessThanGP();
		double[][] operands = { {1,10}, {-100,0}, {-200,-300}, {0,0}, {300,-200}, {100,1} };
		boolean[] expected = { true, true, false, false, false, false };
		biOpTest(n, operands, expected);
	}
	
	public void testGreaterThanGP(){
		GreaterThanGP n = new GreaterThanGP();
		double[][] operands = { {1,10}, {-100,0}, {-200,-300}, {0,0}, {300,-200}, {100,1} };
		boolean[] expected = { false, false, true, false, true, true };
		biOpTest(n, operands, expected);
	}

}
