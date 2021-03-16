package common;
import java.util.*;
import java.util.concurrent.*;

import tokens.*;

public class Evaluator {
  public static ExecutorService executorService = Executors.newFixedThreadPool(16);

  public static double evaluate(HashMap<Token.Identifier, AbstractSyntaxTree.Expr> variables, AbstractSyntaxTree ast) {
    try {
      if (ast instanceof AbstractSyntaxTree.Expr expr) {
        if (ast.children.size() > 1) {
          Future<Double> future = executorService.submit(() -> Evaluator.evaluate(variables, expr.children.get(0)));
          Double op1 = Evaluator.evaluate(variables, expr.children.get(2));
          Double op2 = future.get();
  
          if (expr.children.get(1) instanceof Operator.Add) return op1 + op2;
          else return op1 - op2;
        } else return Evaluator.evaluate(variables, expr.children.get(0));
      } else if (ast instanceof AbstractSyntaxTree.Product product) {
        if (ast.children.size() > 1) {
          Future<Double> future = executorService.submit(() -> Evaluator.evaluate(variables, product.children.get(0)));
          Double op1 = Evaluator.evaluate(variables, product.children.get(2));
          Double op2 = future.get();
          
          if (product.children.get(1) instanceof Operator.Mult) return op1 * op2;
          else if (product.children.get(1) instanceof Operator.Div) return op1 / op2;
          else if (product.children.get(1) instanceof Operator.Pow) return Math.pow(op1, op2);
          else return op1 % op2;
        } else return Evaluator.evaluate(variables, product.children.get(0));
      } else if (ast instanceof AbstractSyntaxTree.Multiplier multiplier) {
        return Evaluator.evaluate(variables, multiplier.children.get(0));
      } else if (ast instanceof Literal.Num num) {
        return num.val;
      } else if (ast instanceof Token.Identifier id) {
        if (variables.containsKey(id))
          return Evaluator.evaluate(variables, variables.get(id));
        else System.out.println("No declared variable " + id.val);
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return 0.;
  } 

  public static double evaluate(AbstractSyntaxTree ast) {
    return Evaluator.evaluate(new HashMap<>(), ast);
  } 
}