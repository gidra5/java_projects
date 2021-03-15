package expr_parser2;

import java.util.ArrayList;
import expr_parser2.*;

public sealed class AbstractSyntaxTree
  permits Token, Expr, Product, Multiplier, Decl, CmdExpr
{
  public ArrayList<AbstractSyntaxTree> children = new ArrayList<>();
}

class FailedToParseException extends Exception {
  private static final long serialVersionUID = 1L;

  FailedToParseException(String errmsg) {
      super(errmsg);
  }
}

public final class CmdExpr extends AbstractSyntaxTree {
  CmdExpr(TokenIterator it) throws FailedToParseException {
    try {
      new Decl((TokenIterator)it.clone()); 

      children.add(new Decl(it));
    } catch (Exception e) {
      children.add(new Expr(it));
    }
  }
}

public final class Expr extends AbstractSyntaxTree {
  Expr(TokenIterator it) throws FailedToParseException {
    children.add(new Product(it));

    // will work when switch pattern matching will be supported
    //
    // switch (it.peek()) { 
    //   case Add, Sub -> ast.children.add(it.next());
    //   default -> { return ast; }
    // }

    if (it.peek() instanceof Add 
     || it.peek() instanceof Sub) 
    {
      children.add(it.next());
      children.add(new Expr(it));
    } 
  }
}

public final class Product extends AbstractSyntaxTree {
  Product(TokenIterator it) throws FailedToParseException {
    children.add(new Multiplier(it).children.get(0));

    if (it.peek() instanceof Mult
     || it.peek() instanceof Div
     || it.peek() instanceof Pow
     || it.peek() instanceof Mod)
    {
      children.add(it.next());
      children.add(new Product(it));
    } 
  }
}

public final class Multiplier extends AbstractSyntaxTree {
  Multiplier(TokenIterator it) throws FailedToParseException {
    if (it.peek() instanceof Identifier || it.peek() instanceof Num) {
      children.add(it.next());
    } else if (it.peek() instanceof Parenthesis.Left) {
      it.next();

      var ast = new Expr(it);

      if (!(it.next() instanceof Parenthesis.Right))
        throw new FailedToParseException("missing closing parenthesis");
      else 
        children.add(ast); 
    } else 
      throw new FailedToParseException("Unexpected token");
  }
}

public final class Decl extends AbstractSyntaxTree {
  Decl(TokenIterator it) throws FailedToParseException {
    children.add(new Identificator(it));

    if (!(it.next() instanceof Equal)) {
      throw new FailedToParseException("Unexpected token");
    }

    children.add(new Expr(it));
  }
}