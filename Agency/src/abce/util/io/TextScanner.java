package abce.util.io;


public interface TextScanner {

	public boolean hasNextChar();



	public boolean hasNextWord();



	public boolean hasNextLine();



	public boolean hasNextInt();



	public boolean hasNextDouble();



	public String nextWord();



	public String nextLine();



	public int nextInt();



	public double nextDouble();



	public String nextChar();

}
