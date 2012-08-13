/***
 * 
 * Test the boolean logic GPNodes to make sure they work correctly.
 * 
 * Created by Matthew Rupp
 */

package abce.test.agency.ec.ecj;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Test;

import abce.agency.ec.ecj.operators.AndGP;
import abce.agency.ec.ecj.operators.NotGP;
import abce.agency.ec.ecj.operators.OrGP;
import abce.agency.ec.ecj.operators.NotAndGP;
import abce.agency.ec.ecj.operators.XOrGP;
import abce.agency.ec.ecj.terminals.FalseGP;
import abce.agency.ec.ecj.terminals.TrueGP;
import abce.agency.ec.ecj.types.BooleanGP;
import ec.gp.GPNode;

public class TestBooleanLogic {

	public static boolean[][] buildTable(int fields){
		int num_rows = (int) Math.pow(2, fields);
		boolean rows[][] = new boolean[num_rows][];
		for (int k = 0; k < num_rows; k++){
			rows[k] = new boolean[fields];
			int place = 2;
			for (int j = 0; j < fields; j++){
				if ( (k/(place/2) ) % place == 0){
					rows[k][j] = false;
				} else {
					rows[k][j] = true;
				}
				place *= 2;
			}
		}
		return rows;
	}
	
	public static boolean[][] btable = buildTable(2);
	
	public static HashMap<Boolean, GPNode> bool2node = new HashMap<Boolean, GPNode>();
	
	{
		bool2node.put(false, new FalseGP());
		bool2node.put(true, new TrueGP());
	}
	
	
	
	@Test
	public void test(){
		//Binary
		testAnd();
		testOr();
		testNotAnd();
		testXor();
		
		//Unary
		testNot();
	}
	
	
	public void testBiLogic(GPNode biOp, boolean[] expected){
		biOp.children = new GPNode[2];
		for (int row=0; row<btable.length; row++){
			biOp.children[0] = bool2node.get(btable[row][0]);
			biOp.children[1] = bool2node.get(btable[row][1]);
			BooleanGP input = new BooleanGP();
			biOp.eval(null, 0, input, null, null, null);
			assertTrue((input.value == expected[row]));
		}
	}
	
	public void testAnd(){
		boolean[] expected = {false, false, false, true};
		testBiLogic(new AndGP(), expected);
	}
	
	public void testOr(){
		boolean[] expected = {false, true, true, true};
		testBiLogic(new OrGP(), expected);
	}
	
	public void testNotAnd(){
		boolean[] expected = {true, true, true, false};
		testBiLogic(new NotAndGP(), expected);
	}
	
	public void testXor(){
		boolean[] expected = {false, true, true, false};
		testBiLogic(new XOrGP(), expected);
	}
	
	public void testNot(){
		NotGP gpnot = new NotGP();
		BooleanGP input = new BooleanGP();
		gpnot.children = new GPNode[1];
		gpnot.children[0] = bool2node.get(false);
		gpnot.eval(null, 0, input, null, null, null);
		assertTrue(input.value);
		gpnot.children[0] = bool2node.get(true);
		gpnot.eval(null, 0, input, null, null, null);
		assertFalse(input.value);
		
		
	}
	
	
	
	public void printTable(){
		for (int j=0; j<btable.length; j++){
			for (int k=0; k<btable[k].length; k++){
				System.err.print(btable[j][k]);
			}
			System.err.print('\n');
		}
	}
}
