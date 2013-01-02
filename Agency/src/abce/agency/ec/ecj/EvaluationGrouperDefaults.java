package abce.agency.ec.ecj;


import ec.DefaultsForm;
import ec.util.Parameter;



public class EvaluationGrouperDefaults implements DefaultsForm {

	public static final String	P_EG	= "grouper";



	public static final Parameter base() {
		return new Parameter(P_EG);
	}
}
