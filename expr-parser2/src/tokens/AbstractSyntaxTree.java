package tokens;

import java.util.*;
import java.util.concurrent.*;

import common.*;
import main.Main;

public sealed class AbstractSyntaxTree implements Cloneable
permits Token,
  AbstractSyntaxTree.DerivativeExpr,
  AbstractSyntaxTree.Expr,
  AbstractSyntaxTree.Product,
  AbstractSyntaxTree.Multiplier,
  AbstractSyntaxTree.Decl,
  AbstractSyntaxTree.CmdExpr
{
  public ArrayList<AbstractSyntaxTree> children = new ArrayList<>();

  public Object clone() {
    try {
      return super.clone();
    } catch (Exception e) {
      return null;
    }
  }

  public static final class CmdExpr extends AbstractSyntaxTree {
    public CmdExpr(TokenIterator it) throws FailedToParseException {
      // if (it.peek() instanceof Keyword.Quit) children.add(it.next());
      // else {
      //   var list = new ArrayList<Callable<AbstractSyntaxTree>>();

      //   list.add(() -> { new Decl((TokenIterator)it.clone()); return new Decl(it); });
      //   list.add(() -> { new Expr((TokenIterator)it.clone()); return new Expr(it); });

      //   try {
      //     children.add(Main.executorService.invokeAny(list));
      //   } catch (InterruptedException | ExecutionException e) { throw (FailedToParseException)e.getCause(); }
      // }

      if (it.peek() instanceof Keyword.Quit) children.add(it.next());
      else {
        try {
          new Decl((TokenIterator)it.clone());

          children.add(new Decl(it));
        } catch (Exception e) {
          children.add(new Expr(it));
        }
      }
    }
  }

  public static final class Decl extends AbstractSyntaxTree {
    public Decl(TokenIterator it) throws FailedToParseException {
      if (!(it.peek() instanceof Token.Identifier)) {
      throw new FailedToParseException("Unexpected token");
    }
      children.add(it.next());

      if (!(it.next() instanceof Operator.Equal)) {
        throw new FailedToParseException("Unexpected token");
      }

      children.add(new Expr(it));
    }
  }

  public static final class DerivativeExpr extends AbstractSyntaxTree {
    public DerivativeExpr(TokenIterator it) throws FailedToParseException {
      if (it.next() instanceof Keyword.Derivative &&
          it.next() instanceof Punct.Brace.Left &&
          it.next() instanceof Token.Identifier ident &&
          it.next() instanceof Punct.Brace.Right)
        children.add(ident);
      else
        throw new FailedToParseException("not of the form \" d[ident] \"");

      if (!(it.next() instanceof Punct.Parenthesis.Left))
        throw new FailedToParseException("missing opening parenthesis");

      var ast = new Expr(it);

      if (it.next() instanceof Punct.Parenthesis.Right)
        children.add(ast);
      else
        throw new FailedToParseException("missing closing parenthesis");
    }
  }

  public static final class Expr extends AbstractSyntaxTree {
    public Expr() {}
    public Expr(TokenIterator it) throws FailedToParseException {
      if (it.peek() instanceof Keyword.Derivative) {
        children.add(new DerivativeExpr(it));
      } else {
        children.add(new Product(it));

        // will work when switch pattern matching will be supported
        //
        // switch (it.peek()) {
        //   case Add, Sub -> ast.children.add(it.next());
        //   default -> { return ast; }
        // }

        if (it.peek() instanceof Operator.Add
         || it.peek() instanceof Operator.Sub)
        {
          children.add(it.next());
          children.add(new Expr(it));
        }
      }
    }
  }

  public static final class Multiplier extends AbstractSyntaxTree {
    public Multiplier() {}
    public Multiplier(TokenIterator it) throws FailedToParseException {
      if (it.peek() instanceof Token.Identifier || it.peek() instanceof Literal.Num) {
        children.add(it.next());
      } else if (it.peek() instanceof Punct.Parenthesis.Left) {
        it.next();

        var ast = new Expr(it);

        if (it.next() instanceof Punct.Parenthesis.Right)
          children.add(ast);
        else
          throw new FailedToParseException("missing closing parenthesis");
      } else
        throw new FailedToParseException("Unexpected token " + it.peek().getClass().getName());
    }
  }

  public static final class Product extends AbstractSyntaxTree {
    public Product() {}
    public Product(TokenIterator it) throws FailedToParseException {
      children.add(new Multiplier(it).children.get(0));

      if (it.peek() instanceof Operator.Mult
       || it.peek() instanceof Operator.Div
       || it.peek() instanceof Operator.Pow
       || it.peek() instanceof Operator.Mod)
      {
        children.add(it.next());
        children.add(new Product(it));
      }
    }
  }
}

class FailedToParseException extends Exception {
  private static final long serialVersionUID = 1L;

  FailedToParseException() {
    super();
  }

  FailedToParseException(String errmsg) {
      super(errmsg);
  }
}
