package ec.agency.io;

import java.util.Collection;


/***
 * A comment block was designed to allow for block-style commenting beginning at any column in an output file.  I
 * t was designed with CommentStrippedInFiles in mind.  It attempts to write a comment 
 * (at a particular offset) marker and then word by word (ws delimited) write to the comment block.
 * 
 * 
 * @author ruppmatt
 *
 */
public class CommentBlock implements FormattedBuffer {
	protected int numLines = 0;  //Number of lines in the comment block
	protected int firstOffset = 0; //The first character offset
	protected int padLen = 0;  //The padding of subsequent lines (or first line if no firstOffset)
	protected StringBuffer curline = new StringBuffer(); //The current line buffer
	protected int linesize = 80; //The size of the line
	protected String cc   = "# "; //The comment delimiter
	protected StringBuffer block = new StringBuffer(); //The size of the current comment block
	
	/**
	 * Create a comment block of 80-character with using the input string.
	 * @param s
	 */
	public CommentBlock(String s){
		this(s,80,0,0);
	}
	
	/**
	 * Create a comment block of variable with using the input string
	 * @param s
	 * @param width
	 */
	public CommentBlock(String s, int width){
		this(s, width, 0, 0);
	}
	
	/**
	 * Create a comment block with a particular space padding on the left side
	 * @param s
	 * @param width
	 * @param pad
	 */
	public CommentBlock(String s, int width, int pad){
		this(s, width, pad, 0);
	}
	
	/**
	 * Create a comment block with a particule space padding on the left side and a
	 * specific offset on the first line (e.g. for adding comments after key/value pairings).
	 * @param s
	 * @param width
	 * @param pad
	 * @param offset
	 */
	public CommentBlock(String s, int width, int pad, int offset){
		linesize = width;
		firstOffset = offset;
		padLen = pad;
		
		Collection<String> words = Utils.getWords(s);
		
		for (String w : words){
			addWord(w);
		}
		if (!noText())
			finishLine();		
	}
	
	/**
	 * Add comment symbol to internal buffer
	 */
	protected void addSym(){
		curline.append(cc);
	}
	
	/**
	 * Return the padding for the current line in the output buffer
	 * @return
	 */
	protected int getPad(){
		return (numLines == 0) ? padLen-firstOffset : padLen; 
	}
	
	/**
	 * Return the offset for the current line
	 * @return
	 */
	protected int getOffset(){
		return (numLines == 0) ? firstOffset : 0;
	}
	
	/**
	 * Check to see if the next word will fit on the current line
	 * @param w
	 * @return
	 */
	protected Boolean wordFits(String w){
		return w.length() <= linesize - getOffset() - curline.length() - 1;
	}
	
	/**
	 * Check to see if the line is empty
	 * @return
	 */
	protected Boolean lineEmpty(){
		return curline.length() == 0;
	}
	
	/**
	 * Considering the comment identifier length, can this word fit?
	 * @param w
	 * @return
	 */
	protected Boolean tooBig(String w){
		return w.length() > linesize - getPad() - cc.length();
	}
	
	/**
	 * Return true if the current line has no text
	 * @return
	 */
	protected Boolean noText(){
		return curline.length() == getPad() + cc.length();
	}
	
	/**
	 * Try to add a word to the comment block
	 * @param w
	 */
	protected void addWord(String w){
		if (lineEmpty()){
			readyLine();
		}
		if (Utils.isNewLine(w)){
			finishLine();
			return;
		}
		if (wordFits(w)){
			if (!noText())
				curline.append(" ");
			curline.append(w);
		} else {  //The word is too big to fit
			if (tooBig(w)){   //The word will always be too big
				if (!noText()){     //If there is text on the line
					finishLine();   // finish the line
				}
				readyLine();
				curline.append(w);
				finishLine();
			} else {        //The word can go on the next line
				finishLine();
				readyLine();
				curline.append(w);
			}
		}
	}
	
	/**
	 * Prepare the line for words
	 */
	protected void readyLine(){
		indentLine();
		addSym();
	}
	
	/**
	 * Indent the line
	 */
	protected void indentLine(){
		curline.append(Utils.rep(" ", getPad()));
	}
	
	/**
	 * Finish the current line
	 */
	protected void finishLine(){
		block.append(curline);
		block.append(endl);
		curline.setLength(0);
		numLines++;
	}
	
	/**
	 * Return a string verison of this comment block buffer
	 */
	public String toString(){
		return block.toString();
	}
	
	/**
	 * Directly return the string buffer comment block
	 */
	public StringBuffer getStringBuffer(){
		return block;
	}
	
	/**
	 * Directly return the string buffer for this comment block
	 */
	public StringBuffer buf(){
		return block;
	}
}
