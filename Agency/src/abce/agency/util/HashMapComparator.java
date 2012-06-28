package abce.agency.util;


import java.util.Comparator;
import java.util.HashMap;



public class HashMapComparator<T> implements Comparator<T> {

	HashMap<T, Integer>	_d;



	public HashMapComparator(HashMap<T, Integer> d) {
		_d = d;
	}



	@Override
	public int compare(T lhs, T rhs) {
		int l = _d.get(lhs);
		int r = _d.get(rhs);
		return (l < r) ? -1 : (l == r) ? 0 : 1;
	}
}
