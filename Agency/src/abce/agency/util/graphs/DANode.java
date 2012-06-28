package abce.agency.util.graphs;


import java.io.Serializable;
import java.util.Collection;

import abce.agency.util.Identifiable;



public class DANode implements Identifiable, Serializable {

	private static final long	serialVersionUID	= 1L;
	DAGraph						_graph				= null;
	String						_name;



	public DANode(DAGraph graph) {
		this(null, graph);
	}



	public DANode(String name, DAGraph graph) {
		_name = name;
		_graph = graph;
		_graph.add(this);
	}



	public int outEdges() {
		return _graph.getConnectedTo(this).size();
	}



	public int inEdges() {
		return _graph.getConnectedFrom(this).size();
	}



	public Collection<DANode> connectedTo() {
		if (_graph != null) {
			return _graph.getConnectedTo(this);
		} else {
			return null;
		}
	}



	public Collection<DANode> connectedFrom() {
		if (_graph != null) {
			return _graph.getConnectedFrom(this);
		} else {
			return null;
		}
	}



	public boolean connectTo(DANode to) {
		if (_graph != null && to.getGraph() == _graph) {
			return _graph.connect(this, to);
		} else
			return false;
	}



	public boolean disconnectTo(DANode to) {
		if (_graph != null && to.getGraph() == _graph) {
			return _graph.disconnect(this, to);
		} else
			return false;
	}



	public boolean connectFrom(DANode from) {
		if (_graph != null && from.getGraph() == _graph) {
			return _graph.connect(from, this);
		} else
			return false;
	}



	public boolean disconnectFrom(DANode from) {
		if (_graph != null && from.getGraph() == _graph) {
			return _graph.disconnect(from, this);
		} else
			return false;
	}



	public DAGraph getGraph() {
		return _graph;
	}



	@Override
	public String getName() {
		return _name;
	}

}
