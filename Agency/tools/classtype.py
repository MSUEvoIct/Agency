class classtype:
  def __init__(self, path):
    self._path = path
    self._super = self._getPackage(path)
    self._name  = self._getName(path)
    self._sub   = self._getSub(path)

  def _getPackage(self,path):
    ndx = path.find('.')
    return path[0:ndx]

  def _getName(self,path):
    ndx = path.rfind('.')
    return path[ndx+1:]

  def _getSub(self,path):
    ndx1 = path.rfind('.')
    if ndx1 == -1:
      return 'None'
    else:
      ndx2 = path.rfind('.', 0, ndx1)
      if ndx2 == -1:
        return 'None'
      else:
        return path[ndx2+1:ndx1]

  def getSuperPackage(self):
    return self._super

  def getPackage(self):
    return self._sub

  def getName(self):
    return self._name

  def getFullName(self):
    return self._path

  def getFullPackage(self):
    ndx = self._path.rfind('.')
    if ndx == -1:
      return 'None'
    else:
      return self._path[0:ndx]


