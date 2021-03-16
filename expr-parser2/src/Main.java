import tokens.*;
import common.*;
import java.util.*;

/*
  Syntax definition:
  decl := ident, "=", expr
  expr := summand, ["-"|"+", expr]
  summand := multiplier, ["*"|"/"|"^"|"%", summand]
  multiplier := number | ident | "(", expr, ")"

  enter expression to evaluate it or i to enter interactive mode
*/

public class Main {
  static HashMap<Token.Identifier, AbstractSyntaxTree.Expr> variables = new HashMap<Token.Identifier, AbstractSyntaxTree.Expr>();

  public static void main(String[] args) {
    try {
      if (args.length == 0) {
        System.out.println("Please enter an arithmetic expression as first argument or enter interactive mode");
        return;
      } else {
        var charIt = new CharIterator(args[0].replace(" ", ""));
        var tokenList = new ArrayList<Token>();

        while (charIt.peek() != '\0')
          tokenList.add((Token)(new Token(charIt).children.get(0)));

        var tokenIt = new TokenIterator(tokenList);
        
        if (tokenIt.peek() instanceof Keyword.InteractiveMode) {
          Scanner scanner = new Scanner(System.in);

          while (true) {
            System.out.print("> ");
            String next_line = scanner.nextLine().replace(" ", "");

            var charIt2 = new CharIterator(next_line);
            var tokenList2 = new ArrayList<Token>();
    
            while (charIt2.peek() != '\0')
              tokenList2.add((Token)(new Token(charIt2).children.get(0)));
    
            var tokenIt2 = new TokenIterator(tokenList2);

            var cmdExpr = new AbstractSyntaxTree.CmdExpr(tokenIt2).children.get(0);

            if (cmdExpr instanceof Keyword.Quit) break;
            else if (cmdExpr instanceof AbstractSyntaxTree.Decl decl) {
              saveVariable(decl);
              printVariables();
            } else if (cmdExpr instanceof AbstractSyntaxTree.Expr expr) 
              System.out.println(Evaluator.evaluate(variables, expr));

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

    Evaluator.executorService.shutdown();
  }

  public static void saveVariable(AbstractSyntaxTree.Decl decl) {
    var ident = (Token.Identifier) decl.children.get(0);
    var expr = (AbstractSyntaxTree.Expr) decl.children.get(1);
    variables.put(ident, expr);
  }

  public static void printVariables() {
    for (Token.Identifier ident : variables.keySet())
      System.out.print(ident.val + " = " + Evaluator.evaluate(variables, variables.get(ident)) + " ");
  }
}
