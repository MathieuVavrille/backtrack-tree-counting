package org.mvavrill.btcounting;

import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;

import java.math.BigInteger;
import java.util.Arrays;

class Node {
  private final Node parent;
  private boolean initDone = false;
  public long size = -1;
  public double nbSolutions = 0;
  public long unsearchedSpace = -1;
  public IntVar var = null;
  public int[] varDomain = null;
  private Node[] nextNodes = null;
  
  
  public Node(final Node parent) {
    this.parent = parent;
    if (parent != null) {
      this.size = parent.size/parent.varDomain.length;
      this.unsearchedSpace = size;
    }
  }

  public boolean isInitialized() {
    return initDone;
  }

  public void init(final IntVar[] vars) {
    size = spaceSize(vars);
    unsearchedSpace = size;
    initDone = true;
  }

  public void initSolution() {
    size = 1;
    nbSolutions = 1;
    unsearchedSpace = 0;
    initDone = true;
  }

  public void initContradiction() {
    size = 0;
    nbSolutions = 0;
    unsearchedSpace = 0;
    initDone = true;
  }

  public void initBranch(final IntVar[] vars) {
    // Currently chooses first non instantiated variable
    int i = 0;
    while (vars[i].isInstantiated()) {
      i++;
      if (i == vars.length)
        return;
    }
    var = vars[i];
    varDomain = new int[var.getDomainSize()];
    nextNodes = new Node[var.getDomainSize()];
    i = 0;
    DisposableValueIterator vit = var.getValueIterator(true);
    while(vit.hasNext()){
      varDomain[i] = vit.next();
      nextNodes[i] = new Node(this);
      i++;
    }
    vit.dispose();
  }

  public Node goToParent() {
    updateData();
    return parent;
  }

  public Node goToChild(final int i) {
    if (nextNodes == null)
      throw new IllegalStateException("Node not initialized before branching");
    return nextNodes[i];
  }

  public int getIdToBranchOn() {
    int bestId = -1;
    long biggestSpace = 0;
    for (int i = 0; i < nextNodes.length; i++) {
      if (nextNodes[i].unsearchedSpace > biggestSpace) {
        biggestSpace = nextNodes[i].unsearchedSpace;
        bestId = i;
      }
    }
    if (bestId == -1)
      throw new IllegalStateException("All the nodes have already been explored");
    return bestId;
  }

  public int getNbSearchedNodes() {
    return (int) Arrays.stream(nextNodes).filter(n -> n != null && n.unsearchedSpace != 0).count();
  }

  private long spaceSize(final IntVar[] vars) {
    long s = 1;
    for (IntVar v: vars)
      s *= v.getDomainSize();
    return s;
  }

  private void updateData() { // Modify because rn the extrapolated number of solution is bs
    if (nextNodes != null) {
      double totalSolutions = 0;
      int nbInitialized = 0;
      for (final Node node : nextNodes) {
        if (node.initDone) {
          nbInitialized++;
          totalSolutions += node.nbSolutions;
        }
      }
      final double ts = totalSolutions;
      final int ni = nbInitialized;
      nbSolutions = Arrays.stream(nextNodes).mapToDouble(node -> (node.initDone) ? node.nbSolutions : ts/ni).sum();
      unsearchedSpace = Arrays.stream(nextNodes).mapToLong(node -> node.unsearchedSpace).sum();
    }
  }

  public void print() {
    print(0);
  }
  public void print(final int depth) {
    for (int i = 0; i < depth; i++) {
      System.out.print("| ");
    }
    System.out.println(((var == null) ? "" : var.getName() + "->" + Arrays.toString(varDomain))  + " " + size + " " + nbSolutions + " " + unsearchedSpace);
    if (nextNodes != null)
      for (final Node node: nextNodes)
        node.print(depth+1);
  }
}
