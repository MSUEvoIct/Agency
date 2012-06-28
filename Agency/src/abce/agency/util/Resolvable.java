package abce.agency.util;


public interface Resolvable {

	public Object resolve(String pathkey, Object ctx, Object... params) throws UnresolvableException;

}
