package main;

import tokens.*;
import common.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/*
  Syntax definition:
  decl := ident, ["(", ident, ")"], "=", expr
  derivative_expr := "d[", ident, "]", expr
  fn_expr := ident, "(", expr, ")"
  expr := derivative_expr | summand, ["-"|"+", expr] | fn_expr
  summand := multiplier, ["*"|"/"|"^"|"%", summand]
  multiplier := number | ident | "(", expr, ")"

  enter expression to evaluate it or i to enter interactive mode
*/

public class Main {
  public static final ExecutorService executorService = Executors.newFixedThreadPool(16);
  public static final ArrayList<AbstractSyntaxTree.Decl> decls = new ArrayList<>();

  public static void main(String[] args) {
    try {
      if (args.length == 0) {
        System.out.println("Please enter an arithmetic expression as first argument or enter interactive mode");
        return;
      } else {
        var tokenIt = str2tokenIt(args[0]);

        if (tokenIt.peek() instanceof Keyword.InteractiveMode) {
          Scanner scanner = new Scanner(System.in);

          while (true) {
            System.out.print("> ");
            String next_line = scanner.nextLine().replace(" ", "");

            tokenIt = str2tokenIt(next_line);

            var cmdExpr = new AbstractSyntaxTree.CmdExpr(tokenIt).children.get(0);

            if (cmdExpr instanceof Keyword.Quit) break;
            else if (cmdExpr instanceof AbstractSyntaxTree.Decl decl) {
              decls.add(decl);
              printVariables();
            } else if (cmdExpr instanceof AbstractSyntaxTree.Expr expr)
              System.out.print(Evaluator.evaluate(expr));

            System.out.println();
          }

          scanner.close();
        } else System.out.println(Evaluator.evaluate(new AbstractSyntaxTree.Expr(tokenIt)));

        System.out.println();
      }
    } catch (Exception e) {
      System.out.println("Failed to parse: " + e.getMessage());
      e.printStackTrace();
    }

    executorService.shutdown();
  }

  public static TokenIterator str2tokenIt(String str) throws FailedToTokenizeException {
    var charIt = new CharIterator(str.replace(" ", ""));
    var tokenList = new ArrayList<Token>();

    while (charIt.peek() != '\0')
      tokenList.add((Token)(new Token(charIt).children.get(0)));

    return new TokenIterator(tokenList);
  }

  public static void printVariables() {
    var it = decls
      .stream()
      .map(decl -> (Callable<Void>)(() -> {
        if (decl.children.get(1) instanceof AbstractSyntaxTree.Expr expr && decl.children.get(0) instanceof Token.Identifier ident) {
          System.out.print(ident.val + " = " + Evaluator.evaluate(expr) + " "); 
          // System.out.println(ident.val + " = " + expr.toString() + " = " + Evaluator.evaluate(expr) + " "); 
        } 
        // else if (decl.children.get(1) instanceof Token.Identifier arg){
        //   System.out.println(ident.val + " " + arg.val + " = " + decl.children.get(2).toString() + " "); 
        // }
        return null; 
      }))
      .collect(Collectors.toList());

    try {
      executorService.invokeAll(it);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
