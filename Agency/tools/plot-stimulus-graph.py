import pygraphviz as pgv
import argparse
import pdb
from collections import deque
import sys
import gzip
import pylab as pl
from descriptor import *
from classtype import *
from strongconnect import *


class GraphDescriptors:

  _clr = {
    'package-package':'red',
    'other-package':'gray',
    'package-class':'#ff8888',
    'other-class':'#888888',
    'package-method':'#ffcccc',
    'other-method':'#cccccc'
      }


  def __init__(self,dcrpts):
    self._descr = dcrpts

  def draw(self,args):
    g = pgv.AGraph(outputorder='edgesfirst',directed=True,overlap='prism1000',sep=+20, esep=+10,pack=False,fontsize=12)
    self._drawNonPackageDescriptors(g,self._descr,args)
    g.layout(prog='neato')
    g.draw(args.output_file[0])


  def _drawNonPackageDescriptors(self,g,descr,args):
    base_package = args.base_package[0]
    nodestyle = {'style':'filled', 'shape':'rectangle'}
    edgestyle = {'Dampening':100,'arrowhead':'normal', 'arrowtail':'crow','dir':'both'}
    classes = {}
    methods = {}
    for d in descr:
      method_name = d.getMethodPkg()+ ":" + d.getMethod()
      dc = classtype(d.getMethodPkg())
      ct = classtype(d.getFullClass())
      rt = classtype(d.getFullReturn())
      at = classtype(d.getFullAssist())
     
      #Add the method's class and package nodes
      clcolor = self._clr['package-class'] if ct.getSuperPackage() == base_package else self._clr['other-class']
      g.add_node(ct.getFullName(), label=ct.getFullName(),fillcolor=clcolor,**nodestyle)
      self._registerClass(ct,classes)

      #Add method
      mtcolor = self._clr['package-method'] if ct.getSuperPackage() == base_package else self._clr['other-method']
      dccolor = self._clr['package-class'] if dc.getSuperPackage() == base_package  else self._clr['other-class']
      g.add_node(method_name, label=d.getName(), color=mtcolor,**nodestyle)
      g.add_node(dc.getFullName(), label=dc.getName(), color=dccolor,**nodestyle)
      g.add_edge(dc.getFullName(), method_name, **edgestyle)
      self._registerMethod(method_name,dc,methods)

      #Add the method's return type class and package nodes
      if at.getName() == 'NullAssistant':
        clcolor = self._clr['package-class'] if rt.getSuperPackage() == base_package  else self._clr['other-class']
        g.add_node(rt.getFullName(), label=rt.getFullName(),fillcolor=clcolor,**nodestyle)
        g.add_edge(method_name, rt.getFullName(), **edgestyle)
        g.add_edge(ct.getFullName(), method_name, **edgestyle)
        self._registerClass(rt,classes)
      else: 
        clcolor = self._clr['package-class'] if at.getSuperPackage() == base_package  else self._clr['other-class']
        g.add_node(at.getFullName(), label=at.getFullName(), fillcolor='cyan',**nodestyle)
        g.add_edge(method_name, at.getFullName(),**edgestyle)
        self._registerClass(at,classes)
    self._highlightTerminals(g)
    self._highlightCycles(g)
    self._connectMethods(g,methods)

  def _highlightTerminals(self,g):
    for n in g.nodes():
      if g.out_degree(n) == 0:
        n.attr['shape']='octagon'
        for e in g.in_edges(n):
          e.attr['Dampening']=0.1

  def _highlightCycles(self,g):
    colors=['cyan', 'green', 'red','blue','orange','purple','brown','yellow']
    clrndx = 0
    nclrs = len(colors)
    sc = strongconnect(g)
    for c in sc.getComponents():
        if len(c) < 2:
          continue
        color = colors[clrndx % nclrs]
        clrndx += 1
        for n in c:
          for s in g.out_neighbors(n):
            if s in c:
              e = g.get_edge(n,s)
              e.attr['color'] = color

  def _registerClass(self,cl,classes):
    pkg = cl.getFullPackage()
    if not pkg in classes.keys():
      classes[pkg] = []
    classes[pkg].append(cl.getFullName())

  def _registerMethod(self,meth,cl,methods):
    cls = cl.getFullName()
    if not cls in methods.keys():
      methods[cls] = []
    methods[cls].append(meth)

  def _connectPackages(self,g,classes):
    npkgs = len(classes.keys())
    pkgs = classes.keys()
    for i in range(npkgs):
      for j in range(i,npkgs):
        g.add_edge(pkgs[i],pkgs[j],color='None',weight=1)
        g.add_edge(pkgs[j],pkgs[i],color='None',weight=1)

  def _connectMethods(self,g,methods):
    for cl in methods.keys():
      meth = methods[cl]
      nmeth = len(meth)
      for i in range(nmeth):
        for j in range(i,nmeth):
          g.add_edge(meth[i],meth[j],color='None',weight=100)
          g.add_edge(meth[j],meth[i],color='None',weight=100)


def setup():
  parser = argparse.ArgumentParser("Plot the graph of stimulus objects from a StimulusWeb output file.  The file should be compressed by default using gzip")
  parser.add_argument('file', help='Gzip compressed file containing stimulus web information.')
  parser.add_argument('-b', '--base-package', nargs=1, required=True, help='The name of the base package (e.g. simternet)')
  parser.add_argument('-o', '--output-file', nargs=1, help='The path to the output file.', default='output-graph.pdf')
  return parser;


if __name__ == "__main__":
  parser = setup()
  args = parser.parse_args()
  path = args.file
  print args
  print path

  
  fin = gzip.open(path)
  descriptors = []
  try:
    descriptors = map(lambda x: descriptor(x) , fin.readlines())
  finally:
    fin.close()
 
  g = GraphDescriptors(descriptors)
  g.draw(args)
