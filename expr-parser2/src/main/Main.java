package main;

import tokens.*;
import common.*;
import java.util.concurrent.*;

/*
  Syntax definition:
  decl            := ident, [ident], "=", expr
  derivative_expr := "d[", ident, "]", multiplier
  fn_expr         := ident, multiplier
  expr            := summand, ["-"|"+", expr]
  summand         := multiplier, ["*"|"/"|"^"|"%", summand]
  multiplier      := number | ident | "(", expr, ")" | derivative_expr | fn_expr

  enter expression to evaluate it or 'i' to enter interactive mode
*/

public class Main {
  public static final ExecutorService executorService = Executors.newFixedThreadPool(16);
  public static final DeclSet decls = new DeclSet();

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Please enter an arithmetic expression or enter interactive mode with 'i'");
      return;
    }

    try {
      var tokenIt = new TokenIterator(args[0]);

      if (tokenIt.peek() instanceof Keyword.InteractiveMode) {
        while (true) {
          tokenIt = new TokenIterator(System.console().readLine("> "));

          var cmdExpr = AbstractSyntaxTree.CmdExpr.parse(tokenIt);

          if (cmdExpr instanceof Keyword.Quit) break;
          else if (cmdExpr instanceof AbstractSyntaxTree.Decl decl) {
            decls.add(decl);
            printDeclarations();
          } else if (cmdExpr instanceof AbstractSyntaxTree.Expr expr) {
            System.out.print(Evaluator.evaluate(expr));

            System.out.println();
          }
        }
      } else System.out.println(Evaluator.evaluate(new AbstractSyntaxTree.Expr(tokenIt)));
    } catch (Exception e) {
      System.out.println("Failed to parse: " + e.getMessage());
      e.printStackTrace();
    }

    executorService.shutdown();
  }

  public static void printDeclarations() {
    decls.parallelStream()
      .forEach(decl -> {
        if (decl.children.get(1) instanceof AbstractSyntaxTree.Expr expr)
          System.out.printf("%s = %f\n", decl, Evaluator.evaluate(expr));
        else System.out.printf("%s\n", decl);
      });
  }
}
