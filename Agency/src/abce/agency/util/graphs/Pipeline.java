package abce.agency.util.graphs;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import abce.agency.util.BadConfiguration;



public class Pipeline extends DAGraph {

	private static final long	serialVersionUID	= 1L;



	public Pipeline() {
		super(null);
	}



	public Pipeline(String name) {
		super(name);
	}



	public Collection<DANode> getEvalOrder() {
		if (!isValid()) {
			return null;
		}
		LinkedList<DANode> order = new LinkedList<DANode>();
		LinkedList<DANode> q = new LinkedList<DANode>();
		for (DANode n : getStart()) {
			q.add(n);
		}
		while (!q.isEmpty()) {
			DANode n = q.poll();
			if (!order.contains(n)) {
				boolean dependencies = true;
				for (DANode from : n.connectedFrom()) {
					if (!order.contains(from))
						dependencies = false;
				}
				if (dependencies == true) {
					order.add(n);
					for (DANode to : n.connectedTo()) {
						if (!order.contains(to) && !q.contains(to)) {
							q.add(to);
						}
					}
				}
			}
		}

		return order;
	}



	public boolean canConnect(DANode from, DANode to) {
		if (_nodes.contains(from) && _nodes.contains(to)) {
			resetVisited();
			BFS(to);
			if (_visited.get(from) == UNVISITED) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}



	public void validate() throws BadConfiguration {
		BadConfiguration bc = new BadConfiguration();
		if (getTerminal().size() != 1) {
			bc.append("Pipeline: more than one terminal is specified.");
		}
		if (!checkStartReachable()) {
			bc.append("Pipeline: not all nodes are reachable from the start.");
		}
		if (!checkTermReachable()) {
			bc.append("Pipeline: not all nodes reach the terminal.");
		}
		bc.validate();
	}



	public boolean isValid() {
		try {
			validate();
			return true;
		} catch (BadConfiguration bc) {
			return false;
		}
	}



	public ArrayList<DANode> getStart() {
		ArrayList<DANode> retval = new ArrayList<DANode>();
		for (DANode n : _nodes) {
			if (n.inEdges() == 0) {
				retval.add(n);
			}
		}
		return retval;
	}



	public ArrayList<DANode> getTerminal() {
		ArrayList<DANode> retval = new ArrayList<DANode>();
		for (DANode n : _nodes) {
			if (n.outEdges() == 0) {
				retval.add(n);
			}
		}
		return retval;
	}



	protected boolean checkStartReachable() {
		resetVisited();
		for (DANode s : getStart()) {
			if (s.inEdges() == 0)
				BFS(s);
		}
		for (Integer i : _visited.values()) {
			if (i == UNVISITED)
				return false;
		}
		return true;
	}



	protected boolean checkTermReachable() {
		ArrayList<DANode> term = getTerminal();

		for (DANode n : _nodes) {
			if (n.outEdges() == 0)
				continue;
			resetVisited();
			BFS(n);
			Boolean hasTerm = false;
			for (DANode t : term) {
				hasTerm = hasTerm || (_visited.get(t) != UNVISITED);
			}
			if (hasTerm == false)
				return false;
		}
		return true;
	}
}
