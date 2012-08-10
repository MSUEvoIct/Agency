package abce.test.util;


import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

import abce.util.io.CommentStrippedInFile;




//TODO: put test file back in place

public class TestWordScanner {

	public static String	path	= "test/test-nocomments.0";
	public final String		words	= "This is a test of a line with a comment\n" + "The second line " + "1 2 3 4 "
											+ "8.5 7.5 3.3";



	@Test
	public void test() {
		Scanner s = new Scanner(words);
		try {
			CommentStrippedInFile fin = new CommentStrippedInFile(path);
			while (s.hasNext()) {
				Assert.assertTrue(fin.hasNextWord());
				String response = fin.nextWord();
				String model = s.next();
				Assert.assertEquals(model, response);
			}
			Assert.assertFalse(fin.hasNextWord());
		} catch (FileNotFoundException e) {
			fail();
		}
	}

}
