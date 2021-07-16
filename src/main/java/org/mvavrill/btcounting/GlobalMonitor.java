package org.mvavrill.btcounting;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.loop.monitors.*;
import org.chocosolver.solver.search.limits.FailCounter;
import org.chocosolver.solver.search.restart.MonotonicRestartStrategy;
import org.chocosolver.solver.variables.IntVar;



class GlobalMonitor implements IMonitorDownBranch, IMonitorUpBranch, IMonitorRestart, IMonitorSolution, IMonitorContradiction {

  private final Solver solver;
  private Node rootNode;
  private Node currentNode;
  private final IntVar[] vars;
  private long currentBudget = 2;
  
  
  public GlobalMonitor(final Solver solver, final IntVar[] vars, final int budget) {
    this.solver = solver;
    solver.plugMonitor(this);
    solver.setRestarts(new BudgetCounter(solver.getModel(), budget), new MonotonicRestartStrategy(budget), Integer.MAX_VALUE);
    rootNode = new Node(null);
    currentNode = rootNode;
    this.vars = vars;
    solver.addStopCriterion( ()-> rootNode.size == rootNode.nbFails + rootNode.nbSolutions || solver.getSolutionCount() + solver.getFailCount() > 100);
  }

  @Override
  public void beforeUpBranch() {}

  @Override
  public void afterUpBranch() {
    currentNode = currentNode.parent();
  }

  @Override
  public void beforeDownBranch(final boolean left) {
    if (left)
      currentNode = currentNode.left();
    else
      currentNode = currentNode.right();
  }

  @Override
  public void afterDownBranch(final boolean left) {
    if (currentNode.size == -1) {
      currentNode.size = spaceSize();
      
    }
  }


  @Override
  public void beforeRestart() {}

  @Override
  public void afterRestart() {
    currentNode = rootNode;
  }

  @Override
  public void onSolution() {
    System.out.println("Solution found");
    for (IntVar v : vars) {
      System.out.print(v);
      System.out.print(" ");
    }
    System.out.println("");
  }

  @Override
  public void onContradiction(ContradictionException ex) {
    currentNode.size = 0;
  }

  private long spaceSize() {
    long s = 1;
    for (IntVar v: vars)
      s *= v.getDomainSize();
    return s;
  }

  public IntVar varSplit() {
    for (IntVar v: vars) {
      if (!v.isInstantiated())
        return v;
    }
    throw new IllegalStateException("All the variables are instantiated");
  }
}
