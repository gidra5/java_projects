package expr-parser2;

import java.util.ArrayList;
import expr-parser2.*;

public sealed interface AbstractSyntaxTree
  permits Token, Expr, Product, Multiplier, Decl
{
  public ArrayList<AbstractSyntaxTree> children = new ArrayList<>();

  public AbstractSyntaxTree parse(TokenIterator it) throws FailedToParseException;
  // todo methods...
}

class FailedToParseException extends Exception {
  private static final long serialVersionUID = 1L;

  FailedToParseException(String errmsg) {
      super(errmsg);
  }
}

public final class Expr implements AbstractSyntaxTree {
  public static AbstractSyntaxTree parse(TokenIterator it) throws FailedToParseException {
    var ast = new Expr();

    ast.children.add(Product.parse(it));

    // switch (it.peek()) {
    //   case Plus t -> ast.children.add(t);
    //   case Minus t -> ast.children.add(t);
    //   default -> { return ast; }
    // }

    if (it.peek() instanceof Add || it.peek() instanceof Sub) {
      ast.children.add(it.next());
    } else { return ast; }

    ast.children.add(Expr.parse(it));

    return ast;
  }
}

public final class Product implements AbstractSyntaxTree {
  public static AbstractSyntaxTree parse(TokenIterator it) throws FailedToParseException {
    var ast = new Product();

    ast.children.add(Multiplier.parse(it));

    // switch (it.peek()) {
    //   case Plus t -> ast.children.add(t);
    //   case Minus t -> ast.children.add(t);
    //   default -> { return ast; }
    // }

    if (it.peek() instanceof Mult
     || it.peek() instanceof Div
     || it.peek() instanceof Pow
     || it.peek() instanceof Mod)
    {
      ast.children.add(it.next());
    } else { return ast; }

    ast.children.add(Product.parse(it));

    return ast;
  }
}

public final class Multiplier implements AbstractSyntaxTree {
  public static AbstractSyntaxTree parse(TokenIterator it) throws FailedToParseException {
    if (it.peek() instanceof Identifier || it.peek() instanceof Num) {
      return it.next();
    } else if(it.peek() instanceof Parenthesis.Left) {
      it.next();

      var ast = Expr.parse(it);

      if (!(it.next() instanceof Parenthesis.Right)) {
        throw new FailedToParseException("missing closing parenthesis");
      } else { return ast; }
    } else {
      throw new FailedToParseException("Unexpected token");
    }
  }
}

public final class Decl implements AbstractSyntaxTree {
  public static AbstractSyntaxTree parse(TokenIterator it) throws FailedToParseException {
    var ast = new Decl();

    ast.children.add(Identificator.parse(it));

    if (!(it.next() instanceof Equal)) {
      throw new FailedToParseException("Unexpected token");
    }

    ast.children.add(Expr.parse(it));

    return ast;
  }
}