# This script reads in a outfile and captures all "tree" representations from
# ECJ.  
# Script requirements:
#     argparse package (should be included in >python2.7)
#     assumes 1-byte characters

import argparse
import pdb
import sys

def setup():
  parser = argparse.ArgumentParser(description='Extracts ECJ trees from stdin or file and prints them one to a line to stdout. Script assumes 1 byte characters.')
  parser.add_argument('file', nargs='?', help='Read from file, not from std in')
  return parser


def readTree(fin):
  #Assuming the first paren alredy read
  level = 1
  tree = '('
  in_sp_block = False
  while level > 0:
    ch = fin.read(1)
    
    #Handle EOF and unnecessary whitespace
    if ch == '':
      print 'Unexpected end of file'
      quit()
    elif ch in '\n\r\t':
      continue
    elif ch == ' ' and in_sp_block:
      continue
    elif ch == ' ' and not in_sp_block:
      in_sp_block = True
    else:
      in_sp_block = False
    
    #Perform leveling
    if ch == ')':
      level = level - 1
    elif ch == '(':
      level = level + 1

    #Append character
    tree = tree + ch
  return tree


def parseTrees(fin):
  trees = []
  at_line_start = True
  while True:
    ch = fin.read(1)
    if ch == '':
      break
    if at_line_start and ch == '(':
      trees.append(readTree(fin))
    elif ch == '\n':
      at_line_start = True
    elif at_line_start and ch in ' \t':
      pass
    else:
      at_line_start = False
  return trees


if __name__ == "__main__":
  parser = setup()
  args = parser.parse_args()
  if args.file != None:
    try:
      fin = open(args.file)
    except:
      print "Unable to open file " + file + " for input."
      quit()
  else:
    fin = sys.stdin
 
  trees = parseTrees(fin)
  
  for tree in trees:
    print tree
  
  if args.file != None:
    fin.close()

