# This file attempts to graph a set of ECJ tree representations
# Requirements:
#        argparse package
#        pygraphviz package
#        nodes.py
#        assuming 1-byte (ASCII) text files
#        assuming 1 tree per line
#        assuming UNIX-style (\n) end of lines


import argparse
import os
import pdb
import sys
import nodes
import pygraphviz as pgv

def setup():
  parser = argparse.ArgumentParser(description="Plot ECJ tree representations.  One tree per line is assumed.  If multiple trees are input, each tree is numbered.  All trees are written to a PDF file.")
  parser.add_argument('file', nargs='?', help='File containing ECJ trees; stdin is used if not file is given.')
  parser.add_argument('-d', '--outdir', nargs=1, help='Directory to output PDF files (default=trees).', default='trees')
  parser.add_argument('-p', '--prefix', nargs=1, help='Output file prefixes (default=tree).', default='tree')
  parser.add_argument('-b', '--begin', nargs=1, type=int, help='First number to begin counting trees at (default=0).', default=0)
  return parser


def readStdIn():
  lines = sys.stdin.read().split('\n')
  return lines if lines[-1] != '' else lines[:-1]


def readFile(path):
  fin = open(path)
  lines = fin.readlines()
  fin.close()
  lines = map(lambda x: x.strip(), lines)
  return lines

def processTree(tree, g, args):
  """
  Parse the tree into a node data structure and pass
  that structure into another method to build the tree
  graph.  
  """
  tree = nodes.NodeBuilder(tree)
  processNode(tree.root(), None, g, args)
  return

def processNode(node, parent, g, args):
  """
  Generate and return the graph for the tree
  one node at a time
  """
  g.add_node(node, label=node.value())
  if parent != None:
    g.add_edge(node,parent)
  for child in node.children():
    processNode(child, node, g, args)
  return

def graphTree(tree, args):
  g = pgv.AGraph(rankdir='RL', directed='true')
  processTree(tree, g, args)
  g.layout(prog='dot')
  return g


if __name__ == '__main__':
  parser = setup()
  args = parser.parse_args()
  trees = readFile(args.file) if args.file != None else readStdIn()
  if not os.path.exists(args.outdir):
    os.mkdir(args.outdir)
  elif not os.path.isdir(args.outdir):
    print args.outdir + ' is not a directory.'
    quit()
  for num,tree in enumerate(trees):
    n = nodes.NodeBuilder(tree)
    print n._str_()
    g = graphTree(tree, args)
    out_path = args.outdir + '/' + args.prefix + '-' + str(num) + '.pdf'
    g.draw(out_path)
