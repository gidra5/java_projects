package tokens;

import java.util.*;
import java.util.stream.*;

import java.util.concurrent.*;

import common.*;
import main.Main;

public interface AbstractSyntaxTree {
  public static AbstractSyntaxTree parse(TokenIterator it) throws FailedToParseException {
    return switch (it.peek().type()) {
      case Let -> DeclNode.parse(it);
      case Simplify -> SimplifyNode.parse(it);
      default -> ExprNode.parse(it);
    };
  }

  public static record FnCallExprNode(Token ident, ExprNode expr) implements ExprNode {
    public ExprNode derivative(Token ident) {
      return switch (ident.src()) {
        case "log" -> ExprNode.template("_/_", expr.derivative(ident), expr);
        default -> this;
      };
    }
    public ExprNode simplify() {
      return switch (ident.src()) {
        case "log" -> {
          if (expr instanceof BinaryInfixExprNode inner)
            yield switch (inner.operator().type()) {
              case Pow -> ExprNode.template("_ * log _", inner.right(), inner.left());
              case Div -> ExprNode.template("log _ - log _", inner.left(), inner.right());
              case Mult -> ExprNode.template("log _ + log _", inner.left(), inner.right());
              default -> new FnCallExprNode(ident, expr.simplify());
            };
          else yield new FnCallExprNode(ident, expr.simplify());
        }
        default -> new FnCallExprNode(ident, expr.simplify());
      };
    }
    public double evaluate(DeclSet decls) {
      return switch (ident.src()) {
        case "log" -> Math.log(expr.evaluate(decls));
        case "floor" -> Math.floor(expr.evaluate(decls));
        default -> {
          var ctx = (DeclSet)decls.clone();
          ctx.add(new DeclNode(decls.get(ident).arg, expr));
    
          yield decls.get(ident).expr.evaluate(ctx);
        }
      };
    }
    public String toString() {
      return ident + " (" + expr + ")";
    }
  }
  public static record UnaryPostfixExprNode(ExprNode expr, Token operator) implements ExprNode {
    public ExprNode derivative(Token ident) {
      return this;
    }
    public ExprNode simplify() {
      return switch (operator.type()) {
        default -> new UnaryPostfixExprNode(expr.simplify(), operator);
      };
    }
    public double evaluate(DeclSet decls) {
      return switch (operator.type()) {
        case Factorial -> ExprNode.factorial(expr.evaluate(decls));
        default -> 0.;
      };
    }
    public String toString() {
      if (expr instanceof Token token) return token.toString() + operator;
      else return "(" + expr + ")" + operator;
    }
  }
  public static record UnaryPrefixExprNode(Token operator, ExprNode expr) implements ExprNode {
    public ExprNode derivative(Token ident) {
      return switch (operator.type()) {
        case Sub -> new UnaryPrefixExprNode(operator, expr.derivative(ident));
        default -> this;
      };
    }
    public ExprNode simplify() {
      return switch (operator.type()) {
        case Sub -> {
          if (expr instanceof Token token && token.equals(new Token(TokenType.Number, "0"))) yield expr;
          else if (expr instanceof UnaryPrefixExprNode inner && inner.operator().type() == TokenType.Sub) yield inner.expr();
          else yield new UnaryPrefixExprNode(operator, expr.simplify());
        }
        default -> new UnaryPrefixExprNode(operator, expr.simplify());
      };
    }
    public double evaluate(DeclSet decls) {
      return switch (operator.type()) {
        case Sub -> -expr.evaluate(decls);
        default -> 0.;
      };
    }
    public String toString() {
      if (expr instanceof Token token) return operator + token.toString();
      else return operator + "(" + expr + ")";
    }
  }
  public static record BinaryInfixExprNode(ExprNode left, Token operator, ExprNode right) implements ExprNode {
    public static BinaryInfixExprNode parse(TokenIterator it, ExprNode left, LinkedList<ExprNode> nodes) throws FailedToParseException {
      var op = it.next();
      return new BinaryInfixExprNode(left, op, ExprNode.parse(it, op.precedence(), nodes));
    }
    public ExprNode simplify() {
      return switch (operator.type()) {
        case Add -> {
          if (left instanceof Token token && token.equals(new Token(TokenType.Number, "0"))) yield right;
          else if (right instanceof Token token && token.equals(new Token(TokenType.Number, "0"))) yield left;
          // else if (left instanceof BinaryInfixExprNode inner 
          //   && inner.operator().type() == TokenType.Mult
          //   && inner.left().equals(right)) yield ExprNode.template("_*(_+1)", right, inner.left());
          // else if (left instanceof BinaryInfixExprNode inner 
          //   && inner.operator().type() == TokenType.Mult
          //   && inner.right().equals(right)) yield ExprNode.template("_*(_+1)", right, inner.right());
          // else if (right instanceof BinaryInfixExprNode inner 
          //   && inner.operator().type() == TokenType.Mult
          //   && inner.left().equals(right)) yield ExprNode.template("_*(_+1)", left, inner.left());
          // else if (right instanceof BinaryInfixExprNode inner 
          //   && inner.operator().type() == TokenType.Mult
          //   && inner.right().equals(right)) yield ExprNode.template("_*(_+1)", left, inner.right());
          else if (left instanceof Token leftToken && right instanceof Token rightToken 
            && leftToken.type() == TokenType.Number && rightToken.type() == TokenType.Number) 
            yield new Token(TokenType.Number, String.valueOf(Float.parseFloat(leftToken.src()) + Float.parseFloat(rightToken.src())));
          else if (left.equals(right)) yield ExprNode.template("2*_", left);
          else yield new BinaryInfixExprNode(left.simplify(), operator, right.simplify());
        }
        case Sub -> {
          if (left instanceof Token token && token.equals(new Token(TokenType.Number, "0"))) yield ExprNode.template("-_", right);
          else if (right instanceof Token token && token.equals(new Token(TokenType.Number, "0"))) yield left;
          else if (left.equals(right)) yield new Token(TokenType.Number, "0");
          else yield new BinaryInfixExprNode(left.simplify(), operator, right.simplify());
        }
        case Mult -> {
          if (left instanceof Token token && token.equals(new Token(TokenType.Number, "0"))) 
            yield new Token(TokenType.Number, "0");
          else if (right instanceof Token token && token.equals(new Token(TokenType.Number, "0"))) 
            yield new Token(TokenType.Number, "0");
          else if (left instanceof Token token && token.equals(new Token(TokenType.Number, "1"))) yield right;
          else if (right instanceof Token token && token.equals(new Token(TokenType.Number, "1"))) yield left;
          else if (left.equals(right)) yield ExprNode.template("_^2", left);
          else yield new BinaryInfixExprNode(left.simplify(), operator, right.simplify());
        }
        case Div -> {
          if (left instanceof Token token && token.equals(new Token(TokenType.Number, "0"))) 
            yield new Token(TokenType.Number, "0");
          else if (right instanceof Token token && token.equals(new Token(TokenType.Number, "1"))) yield left;
          else if (left.equals(right)) yield new Token(TokenType.Number, "1");
          else yield new BinaryInfixExprNode(left.simplify(), operator, right.simplify());
        }
        default -> new BinaryInfixExprNode(left.simplify(), operator, right.simplify());
      };
    }
    public ExprNode derivative(Token ident) {
      return switch (operator.type()) {
        case Add, Sub -> new BinaryInfixExprNode(left.derivative(ident), operator, right.derivative(ident));
        case Mult -> ExprNode.template("_*_+_*_", right.derivative(ident), left, left.derivative(ident), right);
        case Div -> {
          // (1/x)' = -1/x^2*x'
          if (left instanceof Token token && token.equals(new Token(TokenType.Number, "1"))) 
            yield ExprNode.template("-1/_^2*_", right, right.derivative(ident));
          // (y/x)' = (y*1/x)'
          else yield ExprNode.template("1/_*_", right, left).derivative(ident);
        } 
        // (x^y)' = x^(y-1)*(y*x'+x*log x *y')
        case Pow -> ExprNode.template("_1_^(_2_ - 1)*(_2_ * _3_ + _1_ * log _1_ * _4_)", 
          left, 
          right, 
          left.derivative(ident), 
          right.derivative(ident)
        );
        // (x%y)' = x' - y' * floor (x/y)
        case Mod -> ExprNode.template("_ - _ * floor (_/_)", 
          left.derivative(ident), 
          right.derivative(ident),
          left, 
          right
        );
        default -> this;
      };
    }
    public double evaluate(DeclSet decls) {
      return switch (operator.type()) {
        case Add -> left.evaluate(decls) + right.evaluate(decls);
        case Sub -> left.evaluate(decls) - right.evaluate(decls);
        case Mult -> left.evaluate(decls) * right.evaluate(decls);
        case Div -> left.evaluate(decls) / right.evaluate(decls);
        case Pow -> Math.pow(left.evaluate(decls), right.evaluate(decls));
        case Mod -> left.evaluate(decls) % right.evaluate(decls);
        default -> 0.;
      };
    }
    public String toString() {
      var str = "";

      if (left instanceof Token token) str += token;
      else str += "(" + left + ")";
      
      str += " " + operator + " ";

      if (right instanceof Token token) str += token;
      else str += "(" + right + ")";

      return str;
    }
  }
  public static record DerivativeExprNode(Token ident, ExprNode expr) implements ExprNode {
    public static DerivativeExprNode parse(TokenIterator it, LinkedList<ExprNode> nodes) throws FailedToParseException {
      it.next();
      if (it.check(TokenType.LBracket) == null) throw new FailedToParseException("No brackets");

      var tokenNode = it.check(TokenType.Identifier);
      if (tokenNode == null) throw new FailedToParseException("No identifier");

      if (it.check(TokenType.RBracket) == null) throw new FailedToParseException("No brackets");

      return new DerivativeExprNode(tokenNode, ExprNode.parse(it, 6, nodes));
    }
    public ExprNode simplify() {
      return expr.derivative(ident).simplify();
    }
    public ExprNode derivative(Token ident) {
      return expr.derivative(this.ident).derivative(ident);
    }
    public double evaluate(DeclSet decls) {
      var d = expr.derivative(ident);
      System.out.println(d);
      return d.evaluate(decls);
    }
    public String toString() {
      return "d[" + ident + "] (" + expr + ")";
    }
  }

  public static interface ExprNode extends AbstractSyntaxTree {
    public static ExprNode parse(TokenIterator it) throws FailedToParseException {
      return ExprNode.parse(it, 0);
    }
    
    public static ExprNode parse(TokenIterator it, int precedence) throws FailedToParseException {
      return ExprNode.parse(it, precedence, null);
    }

    public static ExprNode parse(TokenIterator it, int precedence, LinkedList<ExprNode> nodes) throws FailedToParseException {
      ExprNode left = switch (it.peek().type()) {
        case Identifier -> {
          var tokenNode = it.next();

          yield switch (it.peek().type()) {
            case Sub, Add, Mult, Div, Pow, Mod, Factorial, RParenthesis, EOT -> tokenNode;
            default -> new FnCallExprNode(tokenNode, ExprNode.parse(it, 6, nodes));
          };
        }
        case Placeholder -> {
          it.next();
          var peek = it.peek(2);
          if (peek.size() == 2 && peek.get(0).type() == TokenType.Number && peek.get(1).type() == TokenType.Placeholder) {
            var index = Integer.parseInt(it.next().src()) - 1;
            it.next();
            yield nodes.get(index);
          } else {
            if (nodes != null && nodes.size() != 0) yield nodes.pollFirst();
            else throw new FailedToParseException("Nothing to substitute");
          }
        }
        case Number -> it.next();
        case Sub -> new UnaryPrefixExprNode(it.next(), ExprNode.parse(it, 4, nodes));
        case LParenthesis -> ExprNode.parseInParenthesis(it, nodes);
        case Derivative -> DerivativeExprNode.parse(it, nodes);
        default -> throw new FailedToParseException("Not expr");
      };

      while (precedence < it.peek().precedence()) {
        left = switch (it.peek().type()) {
          case Sub, Add, Mult, Div, Pow, Mod -> BinaryInfixExprNode.parse(it, left, nodes);
          case Factorial -> new UnaryPostfixExprNode(left, it.next());
          default -> left;
        };
      }

      return left;
    }

    public static ExprNode template(String templateStr, ExprNode... nodes) {
      var it = new TokenIterator(templateStr);
      try {
        return parse(it, 0, new LinkedList(Arrays.asList(nodes)));
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("'" + templateStr + "' is invalid template");
        return new Token(TokenType.None, "");
      }
    }
    ExprNode derivative(Token ident);    
    ExprNode simplify();
    double evaluate(DeclSet decls);

    private static ExprNode parseInParenthesis(TokenIterator it, LinkedList<ExprNode> nodes) throws FailedToParseException {
      it.next();
      var expr = ExprNode.parse(it, 0, nodes);
      if (it.check(TokenType.RParenthesis) == null) throw new FailedToParseException("No closing parenthesis");

      return expr;
    }

    static double factorial(double x) {
      if (x <= 0.) return 1.;
      else return x * factorial(x - 1.);
    }
  }

  public static class DeclNode implements AbstractSyntaxTree {
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

  public static class SimplifyNode implements AbstractSyntaxTree {
    public ExprNode expr;

    public SimplifyNode(ExprNode expr) { 
      this.expr = expr; 
    }
    
    public static SimplifyNode parse(TokenIterator it) throws FailedToParseException {
      if (it.check(TokenType.Simplify) == null) throw new FailedToParseException("No simplify");

      return new SimplifyNode(ExprNode.parse(it));
    }

    public ExprNode simplify() {
      var prev = expr;
      var res = expr.simplify();

      while (!prev.equals(res)) {
        prev = res;
        res = prev.simplify();
      }

      return res;
    }

    public String toString() {
      return "simplify " + expr.toString();
    }
  }
}