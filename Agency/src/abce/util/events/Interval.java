package abce.util.events;


import java.io.Serializable;



public class Interval implements Serializable {

	private static final long	serialVersionUID	= 1L;
	public static final double	FOREVER				= -2.0;
	public static final double	DONE				= -3.0;
	public static final double	NONE				= -1.0;
	public static final double	ATEND				= Double.MAX_VALUE;
	double						_next;
	double						_start;
	double						_interval;
	double						_finish;



	public Interval(double start, double interval, double finish) {
		_start = start;
		_interval = interval;
		_finish = finish;
		_next = start;
	}



	public Interval(String desc) {
		String[] tok = desc.split(":");
		if (tok.length == 3) {
			_start = Double.valueOf(tok[0]);
			_interval = Double.valueOf(tok[1]);
			_finish = tok[2].toLowerCase().equals("end") ? Interval.ATEND : Double.valueOf(tok[2]);
		} else {
			_start = (tok[0].toLowerCase().equals("end")) ? ATEND : Double.valueOf(tok[0]);
			_finish = _start;
			_interval = Interval.NONE;
		}
		_next = _start;
	}



	public double start() {
		return _start;
	}



	public double interval() {
		return _interval;
	}



	public double next() {
		return _next;
	}



	public double finish() {
		return _finish;
	}



	public boolean completed() {
		return _finish != Interval.FOREVER && _finish != Interval.ATEND && _next > _finish;
	}



	public void increment() {
		_next = _next + _interval;
	}



	public boolean willBe(double value) {
		double v = _next;
		while (_finish != Interval.FOREVER && _finish != Interval.ATEND && v <= _finish) {
			if (v == value)
				return true;
			else if (v > value)
				return false;
			v += _interval;
		}
		return false;
	}



	@Override
	public String toString() {
		String prefix = completed() ? "~" : "*";
		return prefix + String.valueOf(_start) + ":" + String.valueOf(_interval) + ":" + String.valueOf(_finish) + "@"
				+ String.valueOf(_next);
	}
}
