package tokens;

import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;

import common.*;
import main.Main;

public class AbstractSyntaxTree {
  public static AbstractSyntaxTree parse(TokenIterator it) throws FailedToParseException {
    return switch (it.peek().type()) {
      case Let -> DeclNode.parse(it);
      default -> ExprNode.parse(it);
    };
  }

  public static class TokenNode extends AbstractSyntaxTree {
    public Token token;

    public TokenNode(Token token) { this.token = token; }

    public static TokenNode parse(TokenIterator it) {
      return new TokenNode(it.next());
    }

    public String toString() {
      return token.toString();
    }
  }

  public static class ExprNode extends AbstractSyntaxTree {
    // fn call: leftOperand ident operator null rightOperand expr
    // ident: leftOperand null operator null rightOperand ident
    // unary postfix: leftOperand expr operator op rightOperand null
    // unary prefix: leftOperand null operator op rightOperand expr
    // binary infix: leftOperand expr operator op rightOperand expr

    public AbstractSyntaxTree leftOperand;
    public Token operator;
    public AbstractSyntaxTree rightOperand;

    public ExprNode(Token operator, AbstractSyntaxTree rightOperand) { 
      this.leftOperand = null; 
      this.operator = operator; 
      this.rightOperand = rightOperand; 
    }

    public ExprNode(AbstractSyntaxTree leftOperand, AbstractSyntaxTree rightOperand) { 
      this.leftOperand = leftOperand; 
      this.operator = null; 
      this.rightOperand = rightOperand; 
    }

    public ExprNode(AbstractSyntaxTree rightOperand) { 
      this.leftOperand = null; 
      this.operator = null; 
      this.rightOperand = rightOperand; 
    }

    public ExprNode(AbstractSyntaxTree leftOperand, Token operator) { 
      this.leftOperand = leftOperand; 
      this.operator = operator; 
      this.rightOperand = null; 
    }

    public ExprNode(AbstractSyntaxTree leftOperand, Token operator, AbstractSyntaxTree rightOperand) { 
      this.leftOperand = leftOperand; 
      this.operator = operator; 
      this.rightOperand = rightOperand; 
    }

    public static ExprNode parse(TokenIterator it) throws FailedToParseException {
      return ExprNode.parse(it, 0);
    }

    public static ExprNode parse(TokenIterator it, int precedence) throws FailedToParseException {
      ExprNode left = switch (it.peek().type()) {
        case Identifier -> {
          var tokenNode = TokenNode.parse(it);

          yield switch (it.peek().type()) {
            case Sub, Add, Mult, Div, Pow, Mod, Factorial, EOT -> new ExprNode(tokenNode);
            default -> new ExprNode(tokenNode, ExprNode.parse(it, 6));
          };
        }
        case Number -> new ExprNode(TokenNode.parse(it));
        case Sub -> new ExprNode(it.next(), ExprNode.parse(it, 4));
        case LParenthesis -> ExprNode.parseInParenthesis(it);
        case Derivative -> ExprNode.parseDerivative(it);
        default -> throw new FailedToParseException("Not expr");
      };

      while (precedence < it.peek().precedence()) {
        left = switch (it.peek().type()) {
          case Sub, Add, Mult, Div, Pow, Mod -> ExprNode.parseInfix(it, left);
          case Factorial -> new ExprNode(left, it.next());
          default -> left;
        };
      }

      return left;
    }

    private static ExprNode parseInfix(TokenIterator it, ExprNode left) throws FailedToParseException {
      var op = it.next();
      return new ExprNode(left, op, ExprNode.parse(it, op.precedence()));
    }

    private static ExprNode parseInParenthesis(TokenIterator it) throws FailedToParseException {
      it.next();
      var expr = ExprNode.parse(it);
      if (it.check(TokenType.RParenthesis) == null) throw new FailedToParseException("No closing parenthesis");

      return expr;
    }

    private static ExprNode parseDerivative(TokenIterator it) throws FailedToParseException {
      it.next();
      
      if (it.check(TokenType.LBracket) == null) throw new FailedToParseException("No brackets");

      var tokenNode = it.check(TokenType.Identifier);
      if (tokenNode == null) throw new FailedToParseException("No identifier");

      if (it.check(TokenType.RBracket) == null) throw new FailedToParseException("No brackets");

      return new ExprNode(tokenNode, ExprNode.parse(it, 6)).derivative();
    }
    
    private ExprNode derivative() throws FailedToParseException {
      return this;
    }
    public String toString() {
      String str = "";

      if (leftOperand != null) {
        if (leftOperand instanceof AbstractSyntaxTree.TokenNode token) str += token.token;
        if (leftOperand instanceof AbstractSyntaxTree.ExprNode expr) {
          if (expr.rightOperand instanceof AbstractSyntaxTree.TokenNode token) str += token.token;
          else str += "(" + expr.toString() + ")";
        }
      }

      str += operator == null ? 
        leftOperand != null && rightOperand != null ? " " : "" 
      : leftOperand == null || rightOperand == null ? operator.toString() : " " + operator.toString() + " ";
 
      if (rightOperand != null) {
        if (rightOperand instanceof AbstractSyntaxTree.TokenNode token) str += token.token;
        else if (rightOperand instanceof AbstractSyntaxTree.ExprNode expr) {
          if (expr.rightOperand instanceof AbstractSyntaxTree.TokenNode token) str += token.token;
          else str += "(" + expr.toString() + ")";
        }
      }

      return str;
    }

    public double evaluate(DeclSet decls) {
      if (leftOperand instanceof TokenNode tokenNode && tokenNode.token.type() == TokenType.Identifier && rightOperand instanceof ExprNode expr) {
        var ident = tokenNode.token;
        var ctx = (DeclSet)decls.clone();
        ctx.add(new DeclNode(decls.get(ident).arg, expr));

        return decls.get(ident).expr.evaluate(ctx);
      } else if (rightOperand instanceof TokenNode tokenNode) {
        if (tokenNode.token.type() == TokenType.Identifier) {
          var ident = tokenNode.token;
  
          return decls.get(ident).expr.evaluate(decls);
        } else return Float.parseFloat(tokenNode.token.src());
      } else if (rightOperand == null && leftOperand instanceof ExprNode expr) {
        return switch (operator.type()) {
          case Factorial -> factorial(expr.evaluate(decls));
          default -> 0.;
        };
      } else if (leftOperand == null && rightOperand instanceof ExprNode expr) {
        return switch (operator.type()) {
          case Sub -> -expr.evaluate(decls);
          default -> 0.;
        };
      } else if (leftOperand instanceof ExprNode left && rightOperand instanceof ExprNode right) {
        return switch (operator.type()) {
          case Add -> left.evaluate(decls) + right.evaluate(decls);
          case Sub -> left.evaluate(decls) - right.evaluate(decls);
          case Mult -> left.evaluate(decls) * right.evaluate(decls);
          case Div -> left.evaluate(decls) / right.evaluate(decls);
          case Pow -> Math.pow(left.evaluate(decls), right.evaluate(decls));
          case Mod -> left.evaluate(decls) % right.evaluate(decls);
          default -> 0.;
        };
      } else return 0.;
    }

    private double factorial(double x) {
      if (x <= 0.) return 1.;
      else return x * factorial(x - 1.);
    }
  }

  public static class DeclNode extends AbstractSyntaxTree {
    public Token ident;
    public Token arg = null;
    public ExprNode expr;

    public DeclNode(Token ident, ExprNode expr) { 
      this.ident = ident; 
      this.expr = expr; 
    }
    
    public DeclNode(Token ident, Token arg, ExprNode expr) { 
      this.ident = ident; 
      this.arg = arg; 
      this.expr = expr; 
    }
    
    public static DeclNode parse(TokenIterator it) throws FailedToParseException {
      if (it.check(TokenType.Let) == null) throw new FailedToParseException("No let");

      var ident = it.check(TokenType.Identifier);
      var arg = it.check(TokenType.Identifier);

      if (ident == null) throw new FailedToParseException("No ident");
      if (it.check(TokenType.Equal) == null) throw new FailedToParseException("No =");

      return new DeclNode(ident, arg, ExprNode.parse(it));
    }

    public void evaluate(DeclSet decls) {
      decls.add(this);
    }

    public String toString() {
      var arg = this.arg == null ? "" : " " + this.arg.toString();
      return "let " + ident.toString() + arg + " = " + expr.toString();
    }

    public boolean equals(Object obj) {
      if (obj instanceof DeclNode d)
        return ident.equals(d.ident);
      else
        return false;
    }

    public int hashCode() {
      return ident.hashCode();
    }
  }
}