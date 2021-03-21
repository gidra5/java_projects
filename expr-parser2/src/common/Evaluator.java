package common;
import java.util.concurrent.*;

import tokens.*;
import main.Main;

public class Evaluator {
  public static double evaluate(AbstractSyntaxTree ast) {
    try {
      if (ast instanceof AbstractSyntaxTree.DerivativeExpr expr) {
        var ident = (Token.Identifier)expr.children.get(0);

        return Evaluator.evaluate(Evaluator.derivative(ident, expr.children.get(1)));
      } else if (ast instanceof AbstractSyntaxTree.Expr expr) {
        if (ast.children.size() > 1) {
          Future<Double> future = Main.executorService.submit(() -> Evaluator.evaluate(expr.children.get(0)));
          Double op2 = Evaluator.evaluate(expr.children.get(2));
          Double op1 = future.get();

          if (expr.children.get(1) instanceof Operator.Add)
            return op1 + op2;
          else
            return op1 - op2;
        } else
          return Evaluator.evaluate(expr.children.get(0));
      } else if (ast instanceof AbstractSyntaxTree.Product product) {
        if (ast.children.size() > 1) {
          Future<Double> future = Main.executorService.submit(() -> Evaluator.evaluate(product.children.get(0)));
          Double op2 = Evaluator.evaluate(product.children.get(2));
          Double op1 = future.get();

          if (product.children.get(1) instanceof Operator.Mult)
            return op1 * op2;
          else if (product.children.get(1) instanceof Operator.Div)
            return op1 / op2;
          else if (product.children.get(1) instanceof Operator.Pow)
            return Math.pow(op1, op2);
          else
            return op1 % op2;
        } else
          return Evaluator.evaluate(product.children.get(0));
      } else if (ast instanceof AbstractSyntaxTree.Multiplier multiplier) {
        return Evaluator.evaluate(multiplier.children.get(0));
      } else if (ast instanceof Literal.Num num) {
        return num.val;
      } else if (ast instanceof Token.Identifier id) {
        if (Main.variables.containsKey(id))
          return Evaluator.evaluate(Main.variables.get(id));
        else
          System.out.println("No declared variable " + id.val);
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return 0.;
  }

  public static AbstractSyntaxTree derivative(Token.Identifier ident, AbstractSyntaxTree ast) {
    try {
      if (ast instanceof AbstractSyntaxTree.DerivativeExpr expr) {
        var ident2 = (Token.Identifier)expr.children.get(0);

        return Evaluator.derivative(ident, Evaluator.derivative(ident2, expr.children.get(1)));
      } else if (ast instanceof AbstractSyntaxTree.Expr expr) {
        if (ast.children.size() > 1) {
          Future<AbstractSyntaxTree> future = Main.executorService.submit(() -> Evaluator.derivative(ident, expr.children.get(0)));
          var op2 = Evaluator.derivative(ident, expr.children.get(2));
          var op1 = future.get();
          var clonedExpr = (AbstractSyntaxTree)expr.clone();

          clonedExpr.children.set(0, op1);
          clonedExpr.children.set(2, op2);

          return clonedExpr;
        } else
          return Evaluator.derivative(ident, expr.children.get(0));
      } else if (ast instanceof AbstractSyntaxTree.Product product) {
        if (ast.children.size() > 1) {
          Future<AbstractSyntaxTree> future = Main.executorService.submit(() -> Evaluator.derivative(ident, product.children.get(0)));
          var op2 = Evaluator.derivative(ident, product.children.get(2));
          var op1 = future.get();

          if (product.children.get(1) instanceof Operator.Mult) {
            var expr = new AbstractSyntaxTree.Expr();
            var prod1 = new AbstractSyntaxTree.Product();
            var prod2 = new AbstractSyntaxTree.Product();

            expr.children.add(prod1);
            expr.children.add(new Operator.Add());
            expr.children.add(prod2);

            prod1.children.add(product.children.get(0));
            prod1.children.add(new Operator.Mult());
            prod1.children.add(op2);

            prod2.children.add(op1);
            prod2.children.add(new Operator.Mult());
            prod2.children.add(product.children.get(2));

            return expr;
          } else if (product.children.get(1) instanceof Operator.Div) {
            var prod = new AbstractSyntaxTree.Product();
            var expr = new AbstractSyntaxTree.Expr();
            var prod1 = new AbstractSyntaxTree.Product();
            var prod2 = new AbstractSyntaxTree.Product();
            var prod3 = new AbstractSyntaxTree.Product();

            prod.children.add(expr);
            prod.children.add(new Operator.Div());
            prod.children.add(prod3);

            expr.children.add(prod2);
            expr.children.add(new Operator.Sub());
            expr.children.add(prod1);

            prod1.children.add(product.children.get(0));
            prod1.children.add(new Operator.Mult());
            prod1.children.add(op2);

            prod2.children.add(op1);
            prod2.children.add(new Operator.Mult());
            prod2.children.add(product.children.get(2));

            prod3.children.add(product.children.get(2));
            prod3.children.add(new Operator.Pow());
            prod3.children.add(new Literal.Num(2.));

            return prod;
          } else if (product.children.get(1) instanceof Operator.Pow) {
            return new Literal.Num(0.); //todo rule for powers
          } else {
            return new Literal.Num(0.); //todo rule for mod
          }
        } else return Evaluator.derivative(ident, product.children.get(0));
      } else if (ast instanceof AbstractSyntaxTree.Multiplier multiplier) {
        return Evaluator.derivative(ident, multiplier.children.get(0));
      } else if (ast instanceof Literal.Num num) {
        return new Literal.Num(0.);
      } else if (ast instanceof Token.Identifier id) {
        if (id.equals(ident)) return new Literal.Num(1.);
        else return Evaluator.derivative(ident, Main.variables.get(id));
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return new Literal.Num(0.);
  }
}