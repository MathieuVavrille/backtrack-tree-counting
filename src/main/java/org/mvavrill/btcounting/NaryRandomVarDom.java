package org.mvavrill.btcounting;

import org.chocosolver.solver.search.strategy.assignments.DecisionOperator;
import org.chocosolver.solver.search.strategy.assignments.DecisionOperatorFactory;
import org.chocosolver.solver.search.strategy.decision.Decision;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class to perform branching decisions over integer variables
 * <br/>
 *
 * @author Charles Prud'homme, Jean-Guillaume Fages
 * @since 2 juil. 2010
 */
public class NaryRandomVarDom extends AbstractStrategy<IntVar> {

  private final Random random;
  
  public NaryRandomVarDom(IntVar[] vars, final Random random) {
    super(vars);
    this.random = random;
  }

  @Override
  public boolean init() {
    return true;
  }

  @Override
  public Decision<IntVar> computeDecision(IntVar variable) {
    if (variable == null || variable.isInstantiated()) {
      return null;
    }
    return new NaryIntDecision(variable, getShuffledDomain(variable));
  }

  @SuppressWarnings({"unchecked"})
  @Override
  public Decision<IntVar> getDecision() {
    List<IntVar> uninstantiatedVars = new ArrayList<IntVar>();
    for (IntVar v: vars)
      if (!v.isInstantiated())
        uninstantiatedVars.add(v);
    return computeDecision(uninstantiatedVars.get(random.nextInt(uninstantiatedVars.size())));
  }

  private int[] getShuffledDomain(final IntVar var) {
    final int[] shuffledDomain = new int[var.getDomainSize()];
    int i = 0;
    DisposableValueIterator vit = var.getValueIterator(true);
    while(vit.hasNext()){
      shuffledDomain[i] = vit.next();
      i++;
    }
    vit.dispose();
    for (int j = shuffledDomain.length-1; j > 0; j--) {
      final int valIndex = random.nextInt(j+1);
      final int oldVal = shuffledDomain[valIndex];
      shuffledDomain[valIndex] = shuffledDomain[j];
      shuffledDomain[j] = oldVal;
    }
    return shuffledDomain;
  }
}
