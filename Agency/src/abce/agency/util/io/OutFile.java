package abce.agency.util.io;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;



public class OutFile {

	protected PrintWriter		_writer;
	protected String			_path;
	public static final String	endl	= System.getProperty("line.separator");



	protected OutFile() {
	}



	public OutFile(String path) throws IOException {
		_path = path;
		FileOutputStream ofstream = new FileOutputStream(new File(_path));
		_writer = new PrintWriter(ofstream);
	}



	public synchronized void write(String s) {
		_writer.print(s);
	}



	public synchronized void writeln(String s) {
		_writer.print(s + endl);

	}



	public PrintWriter getWriter() {
		return _writer;
	}



	public synchronized void close() {
		_writer.flush();
		_writer.close();
	}



	@Override
	protected synchronized void finalize() throws Throwable {
		_writer.close();
	}
}
