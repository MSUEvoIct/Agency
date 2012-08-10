package abce.test.util;


import static org.junit.Assert.fail;

import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;

import abce.util.io.CommentStrippedInFile;




//TODO: Put test file back in place

public class TestNumberScanner {

	public static String	path	= "test/test-nocomments.0";



	@Test
	public void test() {
		CommentStrippedInFile fin;
		try {
			fin = new CommentStrippedInFile(path);
			Assert.assertEquals(fin.nextLine(), "This is a test of a line with a comment ");
			fin.nextLine();
			Assert.assertTrue(fin.hasNextInt());
			Assert.assertTrue(fin.hasNextDouble());
			Assert.assertTrue(fin.hasNextLine());
			Assert.assertTrue(fin.hasNextWord());

			Assert.assertEquals(fin.nextInt(), 1);
			Assert.assertEquals(fin.nextInt(), 2);
			Assert.assertEquals(fin.nextInt(), 3);
			Assert.assertEquals(fin.nextInt(), 4);

			Assert.assertFalse(fin.hasNextInt());
			Assert.assertTrue(fin.hasNextDouble());

			Assert.assertEquals((Double) fin.nextDouble(), (Double) 8.5);
			Assert.assertEquals((Double) fin.nextDouble(), (Double) 7.5);
			Assert.assertEquals((Double) fin.nextDouble(), (Double) 3.3);

			Assert.assertFalse(fin.hasNextDouble());
			Assert.assertFalse(fin.hasNextWord());
			Assert.assertFalse(fin.hasNextLine());
		} catch (FileNotFoundException e) {
			fail();
		}
	}

}
