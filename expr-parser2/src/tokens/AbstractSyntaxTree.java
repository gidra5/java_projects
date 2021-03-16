package tokens;

import java.util.ArrayList;
import common.*;

public sealed class AbstractSyntaxTree
  permits Token, AbstractSyntaxTree.Expr, AbstractSyntaxTree.Product, AbstractSyntaxTree.Multiplier, AbstractSyntaxTree.Decl, AbstractSyntaxTree.CmdExpr
{
  public ArrayList<AbstractSyntaxTree> children = new ArrayList<>();

  public static final class CmdExpr extends AbstractSyntaxTree {
    public CmdExpr(TokenIterator it) throws FailedToParseException {
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

  public static final class Expr extends AbstractSyntaxTree {
    public Expr(TokenIterator it) throws FailedToParseException {
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

  public static final class Multiplier extends AbstractSyntaxTree {
    public Multiplier(TokenIterator it) throws FailedToParseException {
      // System.out.println(it.peek().getClass().getName());

      if (it.peek() instanceof Token.Identifier || it.peek() instanceof Literal.Num) {
        children.add(it.next());
      } else if (it.peek() instanceof Punct.Parenthesis.Left) {
        it.next();
  
        var ast = new Expr(it);
  
        if (!(it.next() instanceof Punct.Parenthesis.Right))
          throw new FailedToParseException("missing closing parenthesis");
        else 
          children.add(ast); 
      } else 
        throw new FailedToParseException("Unexpected token");
    }
  }

  public static final class Product extends AbstractSyntaxTree {
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

  FailedToParseException(String errmsg) {
      super(errmsg);
  }
}

