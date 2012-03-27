package abce.agency.ec.ecj.operators;


import java.lang.reflect.*;

import abce.agency.ec.ecj.types.*;
import ec.*;
import ec.gp.*;
import evoict.*;
import evoict.reflection.*;



/**
 * ActionGP requires a Stimulus-style Problem.
 * 
 * @author ruppmatt
 * 
 */
public class ResponseGP extends GPNode {

	private static final long	serialVersionUID	= 1L;
	Method						m					= null;
	Class<?>[]					arg_types			= null;



	@Override
	public String toString() {
		return "action<" + m.getName() + ">";
	}



	/**
	 * Try to evaluate the response.
	 * 
	 * The method call for the response is identified if it has not already
	 * been.
	 * 
	 * Once the method call is identified, the argument types are identified and
	 * stored.
	 * 
	 * Next, the children are evaluated. The values received by the children are
	 * passed as
	 * arguments to the method call.
	 * 
	 * The problem is *always* the object being used to invoke the method upon.
	 * 
	 */
	@Override
	public void eval(EvolutionState state, int thread, GPData input, ADFStack stack, GPIndividual individual,
			Problem problem) {

		// Setup action method
		if (m == null) {
			try {
				m = ResponseUtils.findResponse(problem);
				arg_types = m.getParameterTypes();
			} catch (BadConfiguration e) {
				System.err.println("Unable to find a response method: " + e.getMessage());
				e.printStackTrace();
				System.exit(1);
			}
		}

		Object[] actual_args = new Object[arg_types.length];

		for (int k = 0; k < actual_args.length; k++) {
			GPData result = ResponseGP.buildResponse(arg_types[k]);
			if (result == null) {
				System.err.println("Unable to create an appropriate GPData type for parameter: " + arg_types[k]
						+ " required for method " + m.getName());
			}
			this.children[k].eval(state, thread, result, stack, individual, problem);
			actual_args[k] = ((Valuable) result).value();
		}

		try {
			m.invoke(problem, actual_args);
		} catch (Exception e) {
			System.err.println("Unable to invoke method " + m.getName() + " using object "
					+ problem.getClass().getName() + ".");
			System.exit(1);
		}

	}



	public static GPData buildResponse(Class<?> type) {
		if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			return new BooleanGP();
		}
		else if (type.equals(double.class) || type.equals(Boolean.class)) {
			return new DoubleGP();
		} else {
			return null;
		}
	}
}
