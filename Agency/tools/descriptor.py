class descriptor:

  def __init__(self, line):
    args = map(lambda x: x.strip(), line.split(','))
    self._cl = args[0]
    self._methpkg, self._meth = args[1].split(':')
    self._ret = args[2]
    self._name = args[3]
    self._assist = args[4]

  def getFullClass(self):
    return self._cl

  def getFullReturn(self):
    return self._ret

  def getFullAssist(self):
    return self._assist

  def getClass(self):
    return self._endOfPath(self._cl)
 
  def getMethod(self):
    return self._meth

  def getMethodPkg(self):
    return self._methpkg

  def getReturn(self):
    return self._endOfPath(self._ret)

  def getName(self):
    return self._name

  def getAssist(self):
    return self._endOfPath(self._assist) 

  def _endOfPath(self, st):
    ndx = st.rfind('.',0,st.rfind('.')-1)
    return st[ndx+1:]

  def getClasses(self):
    return map(lambda x: self._endOfPath(x), [self._cl, self._ret, self._assist])

  def getFullClasses(self):
    return [self._cl, self._ret, self._assist]


