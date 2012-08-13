package abce.agency.ec.ecj.operators;


import java.io.IOException;

import abce.ecj.EPSimpleEvolutionState;
import abce.util.io.DelimitedOutFile;
import ec.EvolutionState;
import ec.gp.GPData;
import ec.gp.GPNode;



/**
 * GPNodeDebug logs debug information about GPNode use.
 * 
 * @author ruppmatt
 * 
 */
public class GPNodeDebug {

	public final static String	V_NODE_FORMAT	= "Generation%d,NodeType%s,NodeName%s,Value%s";



	/**
	 * Log the value produced by a GPNode to a file.
	 * 
	 * @param state
	 * @param thread
	 * @param result
	 * @param node
	 * @param type
	 */
	public static void debug(EvolutionState state, int thread, GPData result, GPNode node, String type) {
		if (state != null){
			EPSimpleEvolutionState eps = (EPSimpleEvolutionState) state;
			if (eps.debug.V_NODE_PROB > 0.0 && eps.generation % eps.debug.V_NODE_MODULO == 0) {
				if (state.random[thread].nextBoolean(eps.debug.V_NODE_PROB)) {
					try {
						DelimitedOutFile fout = eps.file_manager.getDelimitedOutFile(eps.debug.V_NODE_FILEPATH,
								V_NODE_FORMAT);
						fout.write(eps.generation, type, node.toString(), result.toString());
					} catch (IOException e) {
						state.output.warning("Unable to write node data to file.");
					}
	
				}
			}
		}
	}
}
