
class Node:
  def __init__(self, parent):
    self._parent = parent
    self._value = None
    self._children = []

  def addChild(self, child):
    self._children.append(child)
  
  def setValue(self, value):
    self._value = value

  def children(self):
    return self._children
  
  def value(self):
    return self._value


class NodeBuilder:

  def __init__(self, line):
    #print line
    self._buildTree(line)

  def root(self):
    return self._root

  def _buildTree(self, line):
    self._root = None
    cur = self._root
    tokens = iter(line.split(' '))
    stack = [None]
    while True:
      try:
        token = tokens.next()
        if token[0] == '(':
          #print 'Open token for: ' + token
          new_node = Node(cur)
          new_node.setValue(token[1:])
          if self._root == None:
            self._root = new_node
            cur = new_node
          else:
            cur.addChild(new_node)
            stack.append(cur)
            cur = new_node
        else:
          value, closes = self._closeCheck(token)
          if closes > 0:
            #print 'Close ' + str(closes) + ' for token ' + token
            new_node = Node(cur)
            new_node.setValue(value)
            cur.addChild(new_node)
            for k in range(closes):
              cur = stack.pop()
          else:
            new_node = Node(cur)
            new_node.setValue(value)
            cur.addChild(new_node)
            #print 'Non closing token ' + token
      except Exception  as detail:
        #print detail
        break


  def _closeCheck(self, token):
    count = 0
    for ch in token[::-1]:
      if ch == ')':
        count = count + 1
      else:
        break
    value = token[:-1*count] if count > 0 else token
    return value,count




  def _str_(self):
    if self._root == None:
      return ''
    else:
      return self._printNodes()

  
  def _printNodes(self):
    retval = ""
    stack = [(self._root,0)]
    while len(stack) > 0:
      cur,level = stack.pop() 
      retval = retval + '  ' * level + cur.value() + '\n'
      children = cur.children();
      nchild = len(children)
      if nchild > 0:
        stack.extend(tuple(zip(children[::-1],[level+1]*nchild)))
    return retval 
        


