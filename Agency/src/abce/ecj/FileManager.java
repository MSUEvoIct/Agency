package abce.ecj;


import java.io.*;
import java.util.*;

import evoict.io.*;



/**
 * The file manager is an object that keeps track of registered controllers
 * (e.g. simulation objects) and files registered (but not checked to be
 * guaranteed) by those controllers. When the last controller unregisters from
 * the file manager, all registered files are closed.
 * 
 * @author ruppmatt
 * 
 */
public class FileManager {

	/**
	 * 
	 */
	private static final long				serialVersionUID	= 1L;
	protected LinkedHashMap<File, OutFile>	active_files		= new LinkedHashMap<File, OutFile>();
	protected File							root_directory;



	public static File appendPath(File src, String to_add) {
		return new File(src.getPath() + to_add);
	}



	public FileManager() {

	}



	public void initialize(String root_dir_path) throws IOException {
		root_directory = new File(root_dir_path);
		if (root_directory.exists() && !root_directory.isDirectory()) {
			throw new IOException("Root directory " + root_directory + " exists as a regular file.");
		} else if (!root_directory.exists()) {
			root_directory.mkdirs();
		}

	}



	public synchronized DelimitedOutFile getDelimitedOutFile(String path, String format) throws IOException {
		File f = appendPath(root_directory, path);
		if (active_files.containsKey(f)) {
			if (active_files.get(f) instanceof DelimitedOutFile) {
				return (DelimitedOutFile) active_files.get(f);
			} else {
				throw new IOException("File is already open, but it is the wrong type.");
			}
		} else {
			DelimitedOutFile out = new DelimitedOutFile(path, format);
			active_files.put(f, out);
			return out;
		}
	}



	public synchronized DelimitedOutFile getDelimitedOutFile(String path, String format, char delim) throws IOException {
		File f = appendPath(root_directory, path);
		if (active_files.containsKey(f)) {
			if (active_files.get(f) instanceof DelimitedOutFile) {
				return (DelimitedOutFile) active_files.get(f);
			} else {
				throw new IOException("File is already open, but it is the wrong type.");
			}
		} else {
			DelimitedOutFile out = new DelimitedOutFile(path, format, delim);
			active_files.put(f, out);
			return out;
		}
	}



	public synchronized void close(String path) {
		File f = new File(path);
		if (active_files.containsKey(path)) {
			OutFile fot = active_files.get(f);
			fot.close();
			active_files.remove(path);
		}
	}



	/**
	 * Close all registered files
	 */
	protected synchronized void closeAll() {
		for (OutFile out : active_files.values()) {
			out.close();
		}
		active_files.clear();
	}



	@Override
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Open files:\n");
		for (File f : active_files.keySet()) {
			sb.append(String.format("\t%.30s %s\n", active_files.get(f).getClass(), f.getAbsolutePath()));
		}
		return sb.toString();
	}

}
