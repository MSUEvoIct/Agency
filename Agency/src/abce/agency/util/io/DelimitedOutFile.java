package abce.agency.util.io;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;



public class DelimitedOutFile extends GZOutFile {

	protected final char				_delim;
	protected final String				_format;
	protected final int					_numFields;
	protected final ArrayList<String>	_fieldNames	= new ArrayList<String>();
	protected final String				_format_string;



	public DelimitedOutFile(String path, String format) throws IOException {
		this(path, format, ',');
	}



	public DelimitedOutFile(String path, String format, char delim) throws IOException {
		super(path);
		_format = format;
		_delim = delim;
		_format_string = parseFormat(format);
		_numFields = _fieldNames.size();
		printHeader();
	}



	protected String parseFormat(String format) {
		Scanner scan = new Scanner(format).useDelimiter(",");
		StringBuffer fmtstr = new StringBuffer();
		while (scan.hasNext()) {
			String f = scan.next();
			String[] toks = f.split("%");
			fmtstr.append("%" + toks[1]);
			if (scan.hasNext()) {
				fmtstr.append(_delim);
			}
			_fieldNames.add(toks[0]);
		}
		return fmtstr.toString();
	}



	protected void printHeader() {
		write("# " + _format + endl);
		for (int k = 0; k < _fieldNames.size(); k++) {
			if (k > 0) {
				write(_delim + _fieldNames.get(k));
			} else {
				write(_fieldNames.get(k));
			}
		}
		write(endl);
	}



	@Override
	public synchronized void write(String s) {
		_writer.write(s);
	}



	@Override
	public synchronized void writeln(String s) {
		write(s + endl);
	}



	public synchronized void write(Object... fields) {
		write(String.format(_format_string, fields) + endl);
	}

}
