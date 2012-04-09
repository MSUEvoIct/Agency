from collections import deque

class strongconnect:
  def __init__(self, g):
    self._ndx = 0
    self._indeces = {}
    self._lowlink = {}
    self._components = []
    self._s = deque()
    self._do(g)

  def getComponents(self):
    return self._components

  def _do(self, g):
    for node in g.nodes():
      if node not in self._indeces.keys():
        self._strongconnect(g,node)

  def _strongconnect(self,g,node):
    self._indeces[node] = self._ndx
    self._lowlink[node] = self._ndx
    self._s.append(node)
    self._ndx += 1
    #if node.attr['label'] == 'Destination':
    #  pdb.set_trace()
    for succ in g.out_neighbors(node):
      if succ not in self._indeces.keys():
        self._strongconnect(g,succ)
        self._lowlink[node] = min(self._lowlink[node], self._lowlink[succ])
      elif succ in self._s:
        self._lowlink[node] = min(self._lowlink[node], self._indeces[succ])
    if self._lowlink[node] == self._indeces[node]:
      scc = []
      while(self._s[-1] != node):
        scc.append(self._s.pop())
      scc.append(self._s.pop())
      self._components.append(scc)


