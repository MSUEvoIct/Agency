package ec.agency.gp.types;


/**
 * GPData types need to have a common interface to retrieve the value they store
 * internally.
 * 
 * @author ruppmatt
 * 
 */
public interface Valuable {

	public Object value();

}
