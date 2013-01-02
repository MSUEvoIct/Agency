package abce.test.util;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Test;

import ec.agency.reflection.CollectionAssistant;
import ec.agency.reflection.Stimulus;
import ec.agency.reflection.UnrestrictedMethodDictionary;
import ec.agency.util.UnresolvableException;




public class TestMethodDictionary {

	@Test
	public void test() {
		UnrestrictedMethodDictionary derrived_dict = new UnrestrictedMethodDictionary(Derrived.class);
		ArrayList<String> paths = derrived_dict.enumeratePaths(3);

		assertTrue(paths.contains("DerrivedA"));
		assertTrue(paths.contains("DerrivedB"));
		assertTrue(paths.contains("Base"));
		assertTrue(paths.contains("Base.BaseX"));
		assertTrue(paths.contains("Base.BaseY"));
		assertTrue(paths.contains("BaseX"));
		assertTrue(paths.contains("BaseY"));
		assertTrue(paths.contains("FieldZ"));
		assertEquals(paths.size(), 9);

		Derrived d = new Derrived();
		try {
			assertEquals(3.0, derrived_dict.evaluate("DerrivedA", d, (Object[]) null));
			assertEquals(4.0, derrived_dict.evaluate("DerrivedB", d, (Object[]) null));
			assertEquals(1, derrived_dict.evaluate("Base.BaseX", d, (Object[]) null));
			assertEquals(2, derrived_dict.evaluate("Base.BaseY", d, (Object[]) null));
			assertEquals(1, derrived_dict.evaluate("BaseX", d, (Object[]) null));
			assertEquals(2, derrived_dict.evaluate("BaseY", d, (Object[]) null));
			assertEquals(3, derrived_dict.evaluate("FieldZ", d, (Object[]) null));

		} catch (UnresolvableException e) {
			e.printStackTrace();
			fail("Unable to resolve path.");
		}

		try {
			derrived_dict.evaluate("BadResolution", d);
			fail("Bad resolution should fail");
		} catch (UnresolvableException e) {

		}

		UnrestrictedMethodDictionary assist = new UnrestrictedMethodDictionary(MyCollection.class);
		ArrayList<String> apaths = assist.enumeratePaths(6);
		assertFalse(apaths.contains("List"));
		assertFalse(apaths.contains("List.Iterate"));
		assertTrue(apaths.contains("List.Iterate.Value"));
		assertTrue(apaths.contains("List.Iterate.Next.Value"));
		assertTrue(apaths.contains("List.Iterate.Next.Next.Value"));
		assertTrue(apaths.contains("List.Iterate.Next.Next.Next.Value"));
		assertEquals(apaths.size(), 4);

		MyCollection c = new MyCollection();
		try {
			assertEquals(10, assist.evaluate("List.Iterate.Value", c));
			assertEquals(20, assist.evaluate("List.Iterate.Next.Value", c));
			assertEquals(30, assist.evaluate("List.Iterate.Next.Next.Value", c));
			assertEquals(40, assist.evaluate("List.Iterate.Next.Next.Next.Value", c));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Resolutions should all pass.");
		}

	}

	public class Base {

		private final int	x	= 1;
		private final int	y	= 2;

		@Stimulus(name = "FieldZ")
		public final int	z	= 3;



		@Stimulus(name = "BaseX")
		public int getX() {
			return x;
		}



		@Stimulus(name = "BaseY")
		public int getY() {
			return y;
		}
	}

	public class Derrived extends Base {

		private final double	a		= 3.0;
		private final double	b		= 4.0;
		private final Base		capsule	= new Base();



		@Stimulus(name = "DerrivedA")
		public double getA() {
			return a;
		}



		@Stimulus(name = "DerrivedB")
		public double getB() {
			return b;
		}



		double getAB() {
			return a + b;
		}



		@Stimulus(name = "Base")
		public Base getBase() {
			return capsule;
		}

	}

	public class MyCollection {

		ArrayList<Integer>	_arr	= new ArrayList<Integer>();



		public MyCollection() {
			_arr.add(10);
			_arr.add(20);
			_arr.add(30);
			_arr.add(40);
			_arr.add(50);
		}



		@Stimulus(name = "List", assistant = CollectionAssistant.class)
		public ArrayList<Integer> getList() {
			return _arr;
		}
	}



	public Derrived getDerrived() {
		return new Derrived();
	}
}
