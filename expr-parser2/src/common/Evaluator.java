package common;
import java.util.concurrent.*;

import tokens.*;
import main.Main;

// TODO implement templates for easier derivative construction

public class Evaluator {
  public static double evaluate(AbstractSyntaxTree ast) {
    return Evaluator.evaluate(Main.decls, ast);
  }

  public static double evaluate(DeclSet decls, AbstractSyntaxTree ast) {
    try {
      if (ast instanceof AbstractSyntaxTree.DerivativeExpr expr &&
          expr.children.get(0) instanceof Token.Identifier ident) {
        if (expr.children.get(1) instanceof AbstractSyntaxTree.FnExpr fnExpr &&
            fnExpr.children.get(0) instanceof Token.Identifier fnIdent) {

          var variableDecl = decls.get(fnIdent);

          if (variableDecl != null) {
            if (variableDecl.children.get(1) instanceof Token.Identifier arg &&
                decls.clone() instanceof DeclSet declsCopy) {
              var decl = new AbstractSyntaxTree.Decl();
              decl.children.add(arg);
              decl.children.add(fnExpr.children.get(1));

              declsCopy.add(decl);

              return Evaluator.evaluate(decls, Evaluator.derivative(declsCopy, ident, variableDecl.children.get(2)));
            } else System.out.println(fnIdent.val + " is not a function");
          }
        }

        return Evaluator.evaluate(decls, Evaluator.derivative(decls, ident, expr.children.get(1)));
      }

      else if (ast instanceof AbstractSyntaxTree.Expr expr) {
        if (ast.children.size() > 1) {
          Future<Double> future = Main.executorService.submit(() -> Evaluator.evaluate(decls, expr.children.get(0)));
          Double op2 = Evaluator.evaluate(decls, expr.children.get(2));
          Double op1 = future.get();

          if (expr.children.get(1) instanceof Operator.Add)
            return op1 + op2;
          else
            return op1 - op2;
        } else
          return Evaluator.evaluate(decls, expr.children.get(0));
      } else if (ast instanceof AbstractSyntaxTree.Product product) {
        if (ast.children.size() > 1) {
          Future<Double> future = Main.executorService.submit(() -> Evaluator.evaluate(decls, product.children.get(0)));
          Double op2 = Evaluator.evaluate(decls, product.children.get(2));
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
          return Evaluator.evaluate(decls, product.children.get(0));
      } else if (ast instanceof AbstractSyntaxTree.Multiplier multiplier) {
        return Evaluator.evaluate(decls, multiplier.children.get(0));
      } else if (ast instanceof Literal.Num num) {
        return num.val;
      } else if (ast instanceof Token.Identifier id) {
        var variableDecl = decls.get(id);

        if (variableDecl != null) {
          if (variableDecl.children.get(1) instanceof AbstractSyntaxTree.Expr expr)
            return Evaluator.evaluate(decls, expr);
          else
            System.out.println(id.val + " is a function");
        }
      }
      else if (ast instanceof AbstractSyntaxTree.FnExpr expr &&
               expr.children.get(0) instanceof Token.Identifier id) {
        if (id.val.equals("log")) {
          return Math.log(Evaluator.evaluate(decls, expr.children.get(1)));
        } else if (id.val.equals("floor")) {
          return Math.floor(Evaluator.evaluate(decls, expr.children.get(1)));
        } else {
          var variableDecl = decls.get(id);

          if (variableDecl != null) {
            if (variableDecl.children.get(1) instanceof Token.Identifier arg &&
                decls.clone() instanceof DeclSet declsCopy) {
              var decl = new AbstractSyntaxTree.Decl();
              decl.children.add(arg);
              decl.children.add(expr.children.get(1));

              declsCopy.add(decl);

              return Evaluator.evaluate(declsCopy, variableDecl.children.get(2));
            } else System.out.println(id.val + " is not a function");
          }
        }
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return 0.;
  }

  public static AbstractSyntaxTree derivative(DeclSet decls, Token.Identifier ident, AbstractSyntaxTree ast) {
    try {
      if (ast instanceof AbstractSyntaxTree.DerivativeExpr expr) {
        var ident2 = (Token.Identifier)expr.children.get(0);

        return Evaluator.derivative(decls, ident, Evaluator.derivative(decls, ident2, expr.children.get(1)));
      } else if (ast instanceof AbstractSyntaxTree.Expr expr) {
        if (ast.children.size() > 1) {
          Future<AbstractSyntaxTree> future = Main.executorService.submit(() -> Evaluator.derivative(decls, ident, expr.children.get(0)));
          var op2 = Evaluator.derivative(decls, ident, expr.children.get(2));
          var op1 = future.get();
          var clonedExpr = (AbstractSyntaxTree)expr.clone();

          clonedExpr.children.set(0, op1);
          clonedExpr.children.set(2, op2);

          return clonedExpr;
        } else
          return Evaluator.derivative(decls, ident, expr.children.get(0));
      } else if (ast instanceof AbstractSyntaxTree.Product product) {
        if (ast.children.size() > 1) {
          Future<AbstractSyntaxTree> future = Main.executorService.submit(() -> Evaluator.derivative(decls, ident, product.children.get(0)));
          var op2 = Evaluator.derivative(decls, ident, product.children.get(2));
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
            var prod = new AbstractSyntaxTree.Product();
            var prod2 = new AbstractSyntaxTree.Product();
            var prod3 = new AbstractSyntaxTree.Product();
            var prod4 = new AbstractSyntaxTree.Product();
            var prod5 = new AbstractSyntaxTree.Product();
            var multiplier = new AbstractSyntaxTree.Multiplier();
            var expr = new AbstractSyntaxTree.Expr();
            var expr2 = new AbstractSyntaxTree.Expr();
            var fnExpr = new AbstractSyntaxTree.FnExpr();

            multiplier.children.add(expr);

            fnExpr.children.add(new Token.Identifier("log"));
            fnExpr.children.add(product.children.get(0));

            prod.children.add(product.children.get(0));
            prod.children.add(new Operator.Pow());
            prod.children.add(multiplier);

            expr.children.add(product.children.get(2));
            expr.children.add(new Operator.Sub());
            expr.children.add(new Literal.Num(1));

            prod2.children.add(prod);
            prod2.children.add(new Operator.Mult());
            prod2.children.add(expr2);

            expr2.children.add(prod3);
            expr2.children.add(new Operator.Add());
            expr2.children.add(prod4);

            prod3.children.add(product.children.get(2));
            prod3.children.add(new Operator.Mult());
            prod3.children.add(op1);

            prod4.children.add(fnExpr);
            prod4.children.add(new Operator.Mult());
            prod4.children.add(prod5);

            prod5.children.add(op2);
            prod5.children.add(new Operator.Mult());
            prod5.children.add(product.children.get(0));

            return prod2;
          } else {
            var expr = new AbstractSyntaxTree.Expr();
            var prod = new AbstractSyntaxTree.Product();
            var prod2 = new AbstractSyntaxTree.Product();
            var fnExpr = new AbstractSyntaxTree.FnExpr();

            expr.children.add(op1);
            expr.children.add(new Operator.Sub());
            expr.children.add(prod);

            expr.children.add(op1);
            expr.children.add(new Operator.Mult());
            expr.children.add(prod);

            prod.children.add(fnExpr);
            prod.children.add(new Operator.Mult());
            prod.children.add(op2);

            fnExpr.children.add(new Token.Identifier("floor"));
            fnExpr.children.add(prod2);

            prod2.children.add(product.children.get(0));
            prod2.children.add(new Operator.Div());
            prod2.children.add(product.children.get(2));

            return expr;
          }
        } else return Evaluator.derivative(decls, ident, product.children.get(0));
      } else if (ast instanceof AbstractSyntaxTree.Multiplier multiplier) {
        return Evaluator.derivative(decls, ident, multiplier.children.get(0));
      } else if (ast instanceof Literal.Num num) {
        return new Literal.Num(0.);
      } else if (ast instanceof Token.Identifier id) {
        if (id.equals(ident)) return new Literal.Num(1.);
        else {
          var variableDecl = decls.get(id);

          if (variableDecl != null) {
            if (variableDecl.children.get(1) instanceof Token.Identifier arg && arg.equals(ident))
              return Evaluator.derivative(decls, ident, variableDecl.children.get(2));

            return Evaluator.derivative(decls, ident, variableDecl.children.get(1));
          }
        }
      }
      else if (ast instanceof AbstractSyntaxTree.FnExpr expr &&
               expr.children.get(0) instanceof Token.Identifier id) {
        if (id.val.equals("log")) {
          var prod = new AbstractSyntaxTree.Product();

          prod.children.add(Evaluator.derivative(decls, ident, expr.children.get(1)));
          prod.children.add(new Operator.Div());
          prod.children.add(expr.children.get(1));

          return prod;
        } else if (id.val.equals("floor")) return new Literal.Num(0.);
        else {
          var variableDecl = decls.get(id);

          if (variableDecl != null) {
            if (variableDecl.children.get(1) instanceof Token.Identifier arg) {
              Future<AbstractSyntaxTree> future = Main.executorService.submit(() ->
                Evaluator.derivative(decls, arg, variableDecl.children.get(2)));
              // Future<AbstractSyntaxTree> future2 = Main.executorService.submit(() ->
              //   Evaluator.derivative(decls, ident, variableDecl.children.get(2)));
              var op2 = Evaluator.derivative(decls, ident, expr.children.get(1));
              var op1 = future.get();
              // var op3 = future2.get();
              // var sum = new AbstractSyntaxTree.Expr();
              var prod = new AbstractSyntaxTree.Product();

              prod.children.add(op1);
              prod.children.add(new Operator.Mult());
              prod.children.add(op2);

              // sum.children.add(op3);
              // sum.children.add(new Operator.Add());
              // sum.children.add(prod);

              // return sum;
              return prod;
            } else System.out.println(id.val + " is not a function");
          }
        }
      }
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }
    return new Literal.Num(0.);
  }
}