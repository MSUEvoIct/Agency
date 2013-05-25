package ec.agency;

import ec.Individual;
import ec.util.Parameter;

public class NullIndividual extends Individual {
	private static final long serialVersionUID = 1L;

	@Override
	public Parameter defaultBase() {
		return null;
	}

	@Override
	public boolean equals(Object arg0) {
		if (this == arg0)
			return true;
		else
			return false;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

}
