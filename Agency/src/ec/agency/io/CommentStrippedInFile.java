package ec.agency.io;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;



public class CommentStrippedInFile implements TextScanner {

	protected Scanner		_scanner;
	protected final String	eol			= "(\n|\r\n|$)";
	protected final String	delim_word	= "(\\s+|\\s*(#[^" + eol + "]*)+)+";
	protected final String	delim_line	= "((#[^" + eol + "]*)*" + eol + "+)+";
	protected final File	_file;



	public CommentStrippedInFile(String path) throws FileNotFoundException {
		_file = new File(path);
		_scanner = new Scanner(_file);
		_scanner.useDelimiter(delim_word);
	}



	@Override
	public boolean hasNextChar() {
		return _scanner.hasNext(".+");
	}



	@Override
	public boolean hasNextWord() {
		return _scanner.hasNext();
	}



	@Override
	public boolean hasNextLine() {
		_scanner.useDelimiter(delim_line);
		boolean retval = _scanner.hasNext();
		_scanner.useDelimiter(delim_word);
		return retval;
	}



	@Override
	public boolean hasNextInt() {
		return _scanner.hasNextInt();
	}



	@Override
	public boolean hasNextDouble() {
		return _scanner.hasNextDouble();
	}



	@Override
	public String nextWord() {
		String tok = _scanner.next();
		return tok;
	}



	@Override
	public String nextLine() {

		_scanner.useDelimiter(delim_line);
		String tok = _scanner.next();
		while (_scanner.hasNext(delim_line)) {
			_scanner.skip(delim_line);
		}
		if (tok.equals("")) {
			if (hasNextLine()) {
				tok = nextLine();
			}
		}
		_scanner.useDelimiter(delim_word);
		return tok;
	}



	@Override
	public int nextInt() {
		int tok = _scanner.nextInt();
		return tok;
	}



	@Override
	public double nextDouble() {
		double tok = _scanner.nextDouble();
		return tok;
	}



	@Override
	public String nextChar() {
		_scanner.useDelimiter("");
		String tok = _scanner.next(".");
		_scanner.useDelimiter("\\s+");
		return tok;
	}



	public void close() {
		_scanner.close();
	}

}
