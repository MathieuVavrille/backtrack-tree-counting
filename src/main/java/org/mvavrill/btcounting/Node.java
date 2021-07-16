package org.mvavrill.btcounting;

import org.chocosolver.solver.variables.IntVar;

import java.math.BigInteger;

class Node {
  private final Node parent;
  public long size = -1;
  public long nbFails = -1;
  public long nbSolutions = -1;
  public IntVar var = null;
  public int val = 0;
  public int domainSize = 0;
  private Node leftNode = null;
  private Node rightNode = null;
  
  
  public Node(final Node parent) {
    this.parent = parent;
  }

  public Node parent() {
    if (parent == null)
      throw new IllegalStateException("This node does not have a parent");
    return parent;
  }

  public Node left() {
    if (leftNode == null)
      leftNode = new Node(this);
    return leftNode;
  }

  public Node right() {
    if (rightNode == null)
      rightNode = new Node(this);
    return rightNode;
  }
}
