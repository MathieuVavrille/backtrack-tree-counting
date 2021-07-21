package org.mvavrill.btcounting;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import org.javatuples.Pair;

import java.util.Random;

public class BTCountingMain {

  public static void main(String[] args) {
    final Random random = new Random(97);
    Pair<Model,IntVar[]> nqueens = generateQueens(15);
    Model model = nqueens.getValue0();
    IntVar[] vars = nqueens.getValue1();
    Solver solver = model.getSolver();
    GlobalMonitor monitor = new GlobalMonitor(solver, vars, 1, random, false);
    int i = 0;
    while (solver.solve()) {
      System.out.println(i++ + " " + monitor.rootNode.nbSolutions);
    }
    //monitor.rootNode.print();
    System.out.println(i + " " + monitor.rootNode.nbSolutions);
  }

  public static Pair<Model,IntVar[]> generateQueens(final int n) {
    Model model = new Model("NQueens");
    IntVar[] vars = new IntVar[n];
    for (int i = 0; i < vars.length; i++) {
      vars[i] = model.intVar("Q_" + i, 0, n-1, false);
    }
    model.allDifferent(vars, "BC").post();
    for (int i = 0; i < n - 1; i++) {
      for (int j = i + 1; j < n; j++) {
        int k = j - i;
        model.arithm(vars[i], "!=", vars[j], "+", -k).post();
        model.arithm(vars[i], "!=", vars[j], "+", k).post();
      }
    }
    return new Pair<Model,IntVar[]>(model,vars);
  }

}
