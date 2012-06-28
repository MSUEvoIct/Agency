package abce.agency.test;


import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import abce.agency.util.io.DelimitedOutFile;




public class TestDelimitedOutfile {

	public static String	path	= "test/delimited-test.csv.gz";



	@Test
	public void test() {
		String format = "Integer%d,Double%f,String%s,Boolean%b";
		String comment = "# " + format;
		String header = "Integer,Double,String,Boolean";

		DelimitedOutFile fot;
		try {
			fot = new DelimitedOutFile(path, format);
			Boolean b = false;
			Integer i = 2;
			Double d = 10.23;
			String s = "The quick brown fox";
			fot.write(i, d, s, b);
			fot.close();
			FileInputStream istream = new FileInputStream(path);
			GZIPInputStream gzip = new GZIPInputStream(istream);
			InputStreamReader reader = new InputStreamReader(gzip);
			BufferedReader buf = new BufferedReader(reader);
			String test_comment = buf.readLine();
			String test_header = buf.readLine();
			String test_dataline = buf.readLine();
			Assert.assertEquals(test_comment, comment);
			Assert.assertEquals(test_header, header);
			Scanner scan = new Scanner(test_dataline);
			scan.useDelimiter(",");
			Integer test_int = scan.nextInt();
			Double test_double = scan.nextDouble();
			String test_string = scan.next();
			Boolean test_bool = scan.nextBoolean();
			Assert.assertEquals(test_int, i);
			Assert.assertEquals(test_double, d);
			Assert.assertEquals(test_string, s);
			Assert.assertEquals(test_bool, b);
			buf.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			fail();
		}

	}



	@After
	public void after() {
		File outfile = new File(path);
		if (outfile.exists()) {
			outfile.delete();
		}
	}

}
