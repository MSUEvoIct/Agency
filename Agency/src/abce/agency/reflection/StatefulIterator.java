package abce.agency.reflection;


import java.util.Iterator;



public class StatefulIterator<X> {

	protected final Iterator<X>	_it;
	protected X					_state;



	public StatefulIterator(Iterator<X> it) {
		_it = it;
		if (!_it.hasNext()) {
			_state = null;
		} else {
			_state = it.next();
		}
	}



	public void next() {
		if (_it.hasNext()) {
			_state = _it.next();
		}
	}



	public X value() {
		return _state;
	}



	public boolean hasNext() {
		return _it.hasNext();
	}
}
