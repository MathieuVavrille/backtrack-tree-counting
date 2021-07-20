package org.mvavrill.btcounting;

import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.learn.ExplanationForSignedClause;
import org.chocosolver.solver.search.strategy.decision.Decision;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.function.Consumer;

public class RolloutDecision extends Decision<IntVar> {
 
  // The array of decision indices
  private IntVar var;
  private int[] values;
  private final GlobalMonitor monitor;
  private final Node node;

  public RolloutDecision(final GlobalMonitor monitor) {
    super(monitor.getCurrentNode().getNbSearchedNodes());
    this.monitor = monitor;
    this.node = monitor.getCurrentNode();
    this.var = this.node.var;
  }

  @Override
  public Object getDecisionValue() {
    return null;
  }

  @Override
  public void apply() throws ContradictionException {
    if (branch >= 1) {
      int idToBranchOn = node.getIdToBranchOn();
      var.instantiateTo(node.varDomain[idToBranchOn], this);
      System.out.println("branch " + branch + " " + var + "=" + node.varDomain[idToBranchOn]);
      monitor.goToChild(idToBranchOn);
    }
  }

  @Override
  public void free() {}

  @Override
  public String toString() {
    return var.getName();
  }
  
  @Override
  public void explain(int p, ExplanationForSignedClause explanation) {}

  @Override
  public void forEachIntVar(Consumer<IntVar> action) {}

}
