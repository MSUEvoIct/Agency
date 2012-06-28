package abce.agency.util.io;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.zip.GZIPOutputStream;



public class GZOutFile extends OutFile {

	public GZOutFile(String path) throws IOException {
		_path = path;
		FileOutputStream ofstream = new FileOutputStream(new File(_path));
		GZIPOutputStream gzstream = new GZIPOutputStream(ofstream);
		_writer = new PrintWriter(gzstream);
	}

}