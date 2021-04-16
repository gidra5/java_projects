package tokens;

import java.util.*;
import java.util.concurrent.*;

import common.*;
import main.Main;

public sealed abstract class AbstractSyntaxTree implements Cloneable
permits Token,
  AbstractSyntaxTree.DerivativeExpr,
  AbstractSyntaxTree.Expr,
  AbstractSyntaxTree.Product,
  AbstractSyntaxTree.Multiplier,
  AbstractSyntaxTree.Decl,
  AbstractSyntaxTree.CmdExpr,
  AbstractSyntaxTree.FnExpr
{
  public ArrayList<AbstractSyntaxTree> children = new ArrayList<>();

  public Object clone() {
    try {
      return super.clone();
    } catch (Exception e) {
      return null;
    }
  }

  public static final class FnExpr extends AbstractSyntaxTree {
    public FnExpr() {
    }

    public FnExpr(TokenIterator it) throws FailedToParseException {
      children.add(it.check(Token.Identifier.class, "expected identifier"));

      var expr = new Expr();
      var pr = new Product();
      expr.children.add(pr);
      pr.children.add(new Multiplier(it));
      children.add(expr);
    }

    // public static AbstractSyntaxTree parse(TokenIterator it) {
    //   var parsed = new FnExpr();

    //   parsed.children.add(it.check(Token.Identifier.class, "expected identifier"));

    //   it.check(Punct.Parenthesis.Left.class, "expected left parenthesis");

    //   parsed.children.add(new Expr(it));

    //   it.check(Punct.Parenthesis.Right.class, "expected right parenthesis");

    //   return parsed;
    // }

    public String toString() {
      return String.format("%s %s", children.get(0), children.get(1));
    }
  }

  public static final class CmdExpr extends AbstractSyntaxTree {
    public CmdExpr(TokenIterator it) throws FailedToParseException {
      var list = new ArrayList<Callable<AbstractSyntaxTree>>();

      list.add(() -> {
        new Decl((TokenIterator) it.clone());
        return new Decl(it);
      });
      list.add(() -> {
        new Expr((TokenIterator) it.clone());
        return new Expr(it);
      });
      list.add(() -> {
        return it.check(Keyword.Quit.class, "Expected keyword 'quit'");
      });

      try {
        children.add(Main.executorService.invokeAny(list));
      } catch (InterruptedException | ExecutionException e) {
        throw (FailedToParseException) e.getCause();
      }
    }

    public static AbstractSyntaxTree parse(TokenIterator it) throws FailedToParseException {
      // if (it.peek() instanceof Keyword.Quit)
      //   return it.next();

      // try {
      //   return new Decl(it);
      // } catch (Exception e) {  }

      // return new Expr(it);
      return new CmdExpr(it).children.get(0);
    }

    public String toString() {
      return children.get(0).toString();
    }
  }

  public static final class Decl extends AbstractSyntaxTree {
    public Decl() {}

    public Decl(TokenIterator it) throws FailedToParseException {
      it.check(Keyword.Let.class, "Expected keyword let");

      children.add(it.check(Token.Identifier.class, "Expected identifier"));

      try {
        children.add(it.check(Token.Identifier.class, "Expected identifier"));
      } catch (Exception e) {}

      it.check(Operator.Equal.class, "Expected equals symbol");

      children.add(new Expr(it));
    }

    public String toString() {
      if (children.size() >= 3)
        return String.format("%s %s = %s", children.get(0), children.get(1), children.get(2));
      else
        return String.format("%s = %s", children.get(0), children.get(1));
    }

    public boolean equals(Object obj) {
      if (obj instanceof Decl d) {
        return ((Token.Identifier)children.get(0)).equals((Token.Identifier)(d.children.get(0)));
      } else
        return false;
    }

    public int hashCode() {
      return ((Token.Identifier)children.get(0)).val.hashCode();
    }
  }

  public static final class DerivativeExpr extends AbstractSyntaxTree {
    public DerivativeExpr(TokenIterator it) throws FailedToParseException {
      it.check(Keyword.Derivative.class, "Expected keyword 'd'");
      it.check(Punct.Brace.Left.class, "Expected left brace");

      children.add(it.check(Token.Identifier.class, "Expected identifier"));

      it.check(Punct.Brace.Right.class, "Expected right brace");

      children.add(new Expr(it));
    }

    public String toString() {
      return String.format("d[%s] %s = %s",
        children.get(0),
        children.get(1),
        Evaluator.derivative(Main.decls, (Token.Identifier)(children.get(0)), children.get(1)));
    }
  }

  public static final class Expr extends AbstractSyntaxTree {
    public Expr() {}

    public Expr(TokenIterator it) throws FailedToParseException {
      children.add(new Product(it));

      if (it.peek() instanceof Operator.Add ||
          it.peek() instanceof Operator.Sub)
      {
        children.add(it.next());
        children.add(new Expr(it));
      }
    }

    public String toString() {
      if (children.size() == 1) {
        return children.get(0).toString();
      } else
        return String.format("%s %s %s", children.get(0), children.get(1), children.get(2));
    }
  }

  public static final class Multiplier extends AbstractSyntaxTree {
    public Multiplier() {}

    public Multiplier(TokenIterator it) throws FailedToParseException {
      if (it.peek() instanceof Keyword.Derivative) {
        children.add(new DerivativeExpr(it));
        return;
      }

      try {
        new FnExpr((TokenIterator) it.clone());

        children.add(new FnExpr(it));
        return;
      } catch (Exception e) {
      }

      if (it.peek() instanceof Token.Identifier || it.peek() instanceof Literal.Num) {
        children.add(it.next());
        return;
      }

      it.check(Punct.Parenthesis.Left.class, "Expected left parenthesis");

      children.add(new Expr(it));

      it.check(Punct.Parenthesis.Right.class, "Expected right parenthesis");
    }

    public static AbstractSyntaxTree parse(TokenIterator it) throws FailedToParseException {
      return new Multiplier(it).children.get(0);
    }

    public String toString() {
      if (children.get(0) instanceof Expr expr)
        return String.format("(%s)", expr);
      else return children.get(0).toString();
    }
  }

  public static final class Product extends AbstractSyntaxTree {
    public Product() {}
    public Product(TokenIterator it) throws FailedToParseException {
      children.add(new Multiplier(it));

      if (it.peek() instanceof Operator.Mult
       || it.peek() instanceof Operator.Div
       || it.peek() instanceof Operator.Pow
       || it.peek() instanceof Operator.Mod)
      {
        children.add(it.next());
        children.add(new Product(it));
      }
    }

    public String toString() {
      // System.out.println(children.get(0).getClass().getName());
      // System.out.println(children.get(0).toString());
      if (children.size() == 1) {
        return children.get(0).toString();
      } else {
        // System.out.println(children.get(2).getClass().getName());
        // System.out.println(children.get(2).toString());
        return String.format("%s %s %s", children.get(0), children.get(1), children.get(2));
      }
    }
  }
}
