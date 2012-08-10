package abce.util.io;

import java.util.Collection;

public class CommentBlock implements FormattedBuffer {
	protected int numLines = 0;
	protected int firstOffset = 0;
	protected int padLen = 0;
	protected StringBuffer curline = new StringBuffer();
	protected int linesize = 80;
	protected String cc   = "# ";
	protected StringBuffer block = new StringBuffer();
	
	public CommentBlock(String s){
		this(s,80,0,0);
	}
	
	public CommentBlock(String s, int width){
		this(s, 80, 0, 0);
	}
	
	public CommentBlock(String s, int width, int pad){
		this(s, width, pad, 0);
	}
	
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
	
	protected void addSym(){
		curline.append(cc);
	}
	
	protected int getPad(){
		return (numLines == 0) ? padLen-firstOffset : padLen; 
	}
	
	protected int getOffset(){
		return (numLines == 0) ? firstOffset : 0;
	}
	
	protected Boolean wordFits(String w){
		return w.length() <= linesize - getOffset() - curline.length() - 1;
	}
	
	protected Boolean lineEmpty(){
		return curline.length() == 0;
	}
	
	protected Boolean tooBig(String w){
		return w.length() > linesize - getPad() - cc.length();
	}
	
	protected Boolean noText(){
		return curline.length() == getPad() + cc.length();
	}
	
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
	
	protected void readyLine(){
		indentLine();
		addSym();
	}
	
	protected void indentLine(){
		curline.append(Utils.rep(" ", getPad()));
	}
	
	protected void finishLine(){
		block.append(curline);
		block.append(endl);
		curline.setLength(0);
		numLines++;
	}
	
	public String toString(){
		return block.toString();
	}
	
	public StringBuffer getStringBuffer(){
		return block;
	}
	
	public StringBuffer buf(){
		return block;
	}
}
