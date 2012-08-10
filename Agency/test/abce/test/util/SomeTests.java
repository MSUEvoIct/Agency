package abce.test.util;


import org.junit.Test;



public class SomeTests {

	@Test
	public void test() {
		String s = "This has an extra space at the end. ";
		String[] sp = s.split("\\s+");
		for (int k = 0; k < sp.length; k++) {
			System.err.println(k + ": " + sp[k]);
		}
	}

}
