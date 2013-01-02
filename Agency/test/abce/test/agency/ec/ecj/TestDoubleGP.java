/**
 * Test the double arithmetic operations.
 * 
 * Created by Matthew Rupp
 */

package abce.test.agency.ec.ecj;

import static org.junit.Assert.*;

import org.junit.Test;

import ec.EvolutionState;
import ec.Problem;
import ec.agency.gp.operators.AddGP;
import ec.agency.gp.operators.DivideGP;
import ec.agency.gp.operators.MultiplyGP;
import ec.agency.gp.operators.SubtractGP;
import ec.agency.gp.types.DoubleGP;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class TestDoubleGP {
	
	
	
	@Test
	public void test(){
		testAdd();
		testDivide();
		testMultiply();
		testSubtract();
	}
	
	public void biOpTest(GPNode op, double[][] operands, double[] expected){
		op.children = new GPNode[2];
		for (int k=0; k < expected.length; k++){
			op.children[0] = new ConstDoubleNode(operands[k][0]);
			op.children[1] = new ConstDoubleNode(operands[k][1]);
			//System.err.println(op.makeLispTree());
			DoubleGP input = new DoubleGP();
			op.eval(null, 0, input, null, null, null);
			assertEquals(expected[k], input.value, 0.0001);
		}
	}
	
	
	public void testAdd(){
		AddGP n = new AddGP();
		double[][] operands = { {10, 5}, {-20,10}, {30,-15}, {-40,-20} };
		double[] expected = {15, -10, 15, -60};
		biOpTest(n, operands, expected);
	}
	
	public void testDivide(){
		DivideGP n = new DivideGP();
		double[][] operands = { {26.0,2.0}, {0.0,10.0}, {-1.0,2.0}, {2.0,-1.0}, {5.0,0.0} };
		double[] expected = {13.0, 0.0, -0.5, -2.0, 0.0};
		biOpTest(n, operands, expected);
	}
	
	public void testMultiply(){
		MultiplyGP n = new MultiplyGP();
		double[][] operands = { {2.5,3}, {-10.0,5.5}, {5.5,-10.0}, {0,8}, {-1,0}, {-1,-7.5}, {0,0} };
		double[] expected = {7.5, -55.0, -55.0, 0, 0, 7.5, 0.0};
		biOpTest(n, operands, expected);
	}
	
	public void testSubtract(){
		SubtractGP n = new SubtractGP();
		double[][] operands = { {10, 5}, {-20,10}, {30,-15}, {-40,-20} };
		double[] expected = {5, -30, 45, -20};
		biOpTest(n, operands, expected);
	}
}
