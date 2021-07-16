package org.mvavrill.btcounting;

import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.learn.ExplanationForSignedClause;
import org.chocosolver.solver.search.strategy.decision.Decision;
import org.chocosolver.solver.variables.IntVar;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * A decision based on a {@link IntVar}
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 2 juil. 2010
 */
public class NaryIntDecision extends Decision<IntVar> {

  //private static final long serialVersionUID = 4319290465131546449L;
 
  /**
   * The array of decision values
   */
  private int[] values;

  public NaryIntDecision(final IntVar var, final int[] values) {
    super(values.length);
    this.var = var;
    this.values = values;
  }

  @Override
  public int[] getDecisionValue() {
    return values;
  }

  @Override
  public void apply() throws ContradictionException {
    if (branch >= 1) {
      var.getModel().getSolver().getEventObserver().pushDecisionLevel();
      var.instantiateTo(values[branch-1], this);
    }
  }

  @Override
  public void free() {}

  @Override
  public String toString() {
    return var.getName() + "->" + Arrays.toString(values);
  }
  
  @Override
  public void explain(int p, ExplanationForSignedClause explanation) {}

  @Override
  public void forEachIntVar(Consumer<IntVar> action) {}

}
