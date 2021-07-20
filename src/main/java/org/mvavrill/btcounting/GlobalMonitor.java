package org.mvavrill.btcounting;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.search.loop.monitors.*;
import org.chocosolver.solver.search.limits.FailCounter;
import org.chocosolver.solver.search.restart.MonotonicRestartStrategy;
import org.chocosolver.solver.variables.IntVar;

import java.util.Random;


class GlobalMonitor implements IMonitorDownBranch, IMonitorUpBranch, IMonitorRestart, IMonitorSolution, IMonitorContradiction, IMonitorInitialize, IMonitorOpenNode {

  private final Solver solver;
  private final Node rootNode;
  private Node currentNode;
  private final IntVar[] vars;
  private final Random random;
  
  
  public GlobalMonitor(final Solver solver, final IntVar[] vars, final int budget, final Random random) {
    this.solver = solver;
    currentNode = rootNode = new Node(null);
    this.vars = vars;
    this.random = random;
    solver.plugMonitor(this);
    solver.setRestarts(new BudgetCounter(solver.getModel(), budget), new MonotonicRestartStrategy(budget), Integer.MAX_VALUE);
    solver.setSearch(new RolloutStrategy(this));
    solver.addStopCriterion( ()-> rootNode.unsearchedSpace == 0 || solver.getSolutionCount() + solver.getFailCount() > 100);
  }

  public void goToChild(final int i) {
    currentNode = currentNode.goToChild(i);
  }

  @Override
  public void beforeOpenNode() {
    System.out.println("OpenNode");
    if (currentNode.var == null)
      currentNode.initBranch(vars);
  }

  @Override
  public void afterUpBranch() {
    System.out.println("afterUpBranch");
    currentNode = currentNode.goToParent();
  }

  @Override
  public void afterDownBranch(final boolean left) {
    System.out.println("afterDownBranch");
    if (!currentNode.isInitialized()) {
      currentNode.print();
      currentNode.init(vars);
    }
  }

  @Override
  public void afterInitialize(final boolean correct) {
    System.out.println("afterInitialize");
    afterDownBranch(true);
  }

  @Override
  public void afterRestart() { // Goes back to top, by calling goToParent (which will compute unsearched spaces and number of solutions)
    System.out.println("afterRestart");
    boolean isFinished = false;
    while (!isFinished) {
      Node parentNode = currentNode.goToParent();
      if (parentNode == null)
        isFinished = true;
      else
        currentNode = parentNode;
    }
    rootNode.print();
    System.out.println(currentNode);
    System.out.println("\n\n");
  }

  @Override
  public void onSolution() {
    System.out.println("onSolution");
    currentNode.initSolution();
  }

  @Override
  public void onContradiction(ContradictionException ex) {
    System.out.println("onContradiction");
    currentNode.initContradiction();
  }

  public IntVar[] getVars() {
    return vars;
  }
  
  public Random getRandom() {
    return random;
  }

  public Node getCurrentNode() {
    return currentNode;
  }
  
}
