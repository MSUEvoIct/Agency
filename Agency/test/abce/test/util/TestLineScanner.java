package abce.test.util;


import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import ec.agency.io.CommentStrippedInFile;





//TODO: put test file back in place

public class TestLineScanner {

	public static String		path	= "test/test-nocomments.0";
	public ArrayList<String>	models	= new ArrayList<String>();



	@Test
	public void test() {
		models.add("This is a test of a line with a comment ");
		models.add("The  second line");
		models.add("1 2 3 4 ");
		models.add("8.5 7.5 3.3");
		CommentStrippedInFile fin;
		try {
			fin = new CommentStrippedInFile(path);
			for (String model : models) {
				Assert.assertTrue(fin.hasNextLine());
				String response = fin.nextLine();
				Assert.assertEquals(model, response);
			}
			Assert.assertFalse(fin.hasNextLine());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		}

	}

}
