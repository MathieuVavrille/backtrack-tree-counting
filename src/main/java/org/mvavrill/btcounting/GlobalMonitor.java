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
  public final Node rootNode;
  private Node currentNode;
  private final IntVar[] vars;
  private final Random random;
  private final boolean verbose;
  
  
  public GlobalMonitor(final Solver solver, final IntVar[] vars, final int budget, final Random random, final boolean verbose) {
    this.solver = solver;
    currentNode = rootNode = new Node(null);
    this.vars = vars;
    this.random = random;
    this.verbose = verbose;
    solver.plugMonitor(this);
    solver.setRestarts(new BudgetCounter(solver.getModel(), budget), new MonotonicRestartStrategy(budget), Integer.MAX_VALUE);
    solver.setSearch(new RolloutStrategy(this));
    solver.addStopCriterion( ()-> rootNode.unsearchedSpace == 0);
  }

  public void goToChild(final int i) {
    currentNode = currentNode.goToChild(i);
  }

  @Override
  public void beforeOpenNode() {
    if (verbose)
      System.out.println("beforeOpenNode");
    if (currentNode.var == null)
      currentNode.initBranch(vars);
  }
  @Override
  public void afterOpenNode() {if (verbose) System.out.println("afterOpenNode");}
  
  @Override
  public void beforeUpBranch() {
    if (verbose)
      System.out.println("beforeUpBranch");
    currentNode = currentNode.goToParent();
  }
  @Override
  public void afterUpBranch() {if (verbose) System.out.println("afterUpBranch");}

  @Override
  public void beforeDownBranch(final boolean left) {if (verbose) System.out.println("beforeDownBranch");}
  @Override
  public void afterDownBranch(final boolean left) {
    if (verbose)
      System.out.println("afterDownBranch");
    if (!currentNode.isInitialized()) {
      //currentNode.print();
      currentNode.init(vars);
    }
  }

  @Override
  public void afterInitialize(final boolean correct) {
    if (verbose)
      System.out.println("afterInitialize");
    afterDownBranch(true);
  }

  @Override
  public void beforeRestart() {if (verbose) System.out.println("beforeRestart");}
  @Override
  public void afterRestart() { // Goes back to top, by calling goToParent (which will compute unsearched spaces and number of solutions)
    if (verbose)
      System.out.println("afterRestart");
    boolean isFinished = false;
    while (!isFinished) {
      Node parentNode = currentNode.goToParent();
      if (parentNode == null)
        isFinished = true;
      else
        currentNode = parentNode;
    }
    if (verbose) {
      rootNode.print();
      System.out.println("\n\n");
    }
  }

  @Override
  public void onSolution() {
    if (verbose)
      System.out.println("onSolution");
    currentNode.initSolution();
  }

  @Override
  public void onContradiction(ContradictionException ex) {
    if (verbose)
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
