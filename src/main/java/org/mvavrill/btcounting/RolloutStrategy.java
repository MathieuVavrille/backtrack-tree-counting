package org.mvavrill.btcounting;

import org.chocosolver.solver.search.strategy.decision.Decision;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class RolloutStrategy extends AbstractStrategy<IntVar> {

  private final GlobalMonitor monitor;
  
  public RolloutStrategy(final GlobalMonitor monitor) {
    super(monitor.getVars());
    this.monitor = monitor;
  }

  @Override
  public boolean init() {
    return true;
  }

  @Override
  public Decision<IntVar> computeDecision(IntVar variable) {
    if (variable == null || variable.isInstantiated() || variable != monitor.getCurrentNode().var) {
      return null;
    }
    return new RolloutDecision(monitor);
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public Decision<IntVar> getDecision() {
    if (monitor.getCurrentNode().var == null)
      return null;
    return new RolloutDecision(monitor);
  }
}
