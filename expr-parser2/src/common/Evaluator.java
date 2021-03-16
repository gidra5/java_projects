package common;
import java.util.concurrent.*;

import tokens.*;
import main.Main;

public class Evaluator {
  public static double evaluate(AbstractSyntaxTree ast) {
    try {
      if (ast instanceof AbstractSyntaxTree.Expr expr) {
        if (ast.children.size() > 1) {
          Future<Double> future = Main.executorService.submit(() -> Evaluator.evaluate(expr.children.get(0)));
          Double op2 = Evaluator.evaluate(expr.children.get(2));
          Double op1 = future.get();
  
          if (expr.children.get(1) instanceof Operator.Add) return op1 + op2;
          else return op1 - op2;
        } else return Evaluator.evaluate(expr.children.get(0));
      } else if (ast instanceof AbstractSyntaxTree.Product product) {
        if (ast.children.size() > 1) {
          Future<Double> future = Main.executorService.submit(() -> Evaluator.evaluate(product.children.get(0)));
          Double op2 = Evaluator.evaluate(product.children.get(2));
          Double op1 = future.get();
          
          if (product.children.get(1) instanceof Operator.Mult) return op1 * op2;
          else if (product.children.get(1) instanceof Operator.Div) return op1 / op2;
          else if (product.children.get(1) instanceof Operator.Pow) return Math.pow(op1, op2);
          else return op1 % op2;
        } else return Evaluator.evaluate(product.children.get(0));
      } else if (ast instanceof AbstractSyntaxTree.Multiplier multiplier) {
        return Evaluator.evaluate(multiplier.children.get(0));
      } else if (ast instanceof Literal.Num num) {
        return num.val;
      } else if (ast instanceof Token.Identifier id) {
        if (Main.variables.containsKey(id))
          return Evaluator.evaluate(Main.variables.get(id));
        else System.out.println("No declared variable " + id.val);
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return 0.;
  } 
}