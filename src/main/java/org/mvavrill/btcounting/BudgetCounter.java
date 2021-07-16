package org.mvavrill.btcounting;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.search.limits.ACounter;
import org.chocosolver.solver.search.measure.IMeasures;

/**
 * Counter for the fails and solutions
 */
public class BudgetCounter extends ACounter {

  public BudgetCounter(Model model, long budget) {
    this(model.getSolver().getMeasures(), budget);
  }

  public BudgetCounter(IMeasures measures, long budget) {
    super(measures, budget);
  }

  @Override
  public long currentValue() {
    return measures.getSolutionCount() + measures.getFailCount();
  }
}
