package expr-parser2;
import expr-parser2.tokens.*;

/*
  Syntax definition:
  decl := ident, "=", expr
  expr := summand, ["-"|"+", expr]
  summand := multiplier, ["*"|"/"|"^"|"%", summand]
  multiplier := number | ident | "(", expr, ")"

  enter expression to evaluate it or i to enter interactive mode
*/

public class Main {
  static HashMap<Identificator, Expr> variables = new HashMap<Identificator, Expr>();

  public static void main(String[] args) {
    try {
      if (args.length == 0) {
        System.out.println("Please enter an arithmetic expression as first argument or enter interactive mode");
        return;
      } else {
        var charIt = new CharIterator(args[0].replace(" ", ""));
        var tokenList = new ArrayList();

        while (charIt.peek() != '\0')
          tokenList.add(Token.tokenize(charIt));

        var tokenIt = new TokenIterator(tokenList);
        
        if (tokenIt.peek() instanceof InteractiveMode) {
          Scanner scanner = new Scanner(System.in);

          while (true) {
            System.out.println("> ");
            String next_line = scanner.nextLine().replace(" ", "");

            var charIt = new CharIterator(next_line);
            var tokenList = new ArrayList();

            while (charIt.peek() != '\0')
              tokenList.add(Token.tokenize(charIt));

            var tokenIt = new TokenIterator(tokenList);

            var cmdExpr = new CmdExpr(tokenIt).children.get(0);

            if (cmdExpr instanceof Quit) {

            } else if (cmdExpr instanceof Decl decl) {
              saveVariable(decl);
              printVariables();
            } else if (cmdExpr instanceof Expr expr) 
              System.out.println(Evaluator.evaluate(expr));

            System.out.println();
          }

          scanner.close();
        } else 
          System.out.println(Evaluator.evaluate(new Expr(tokenIt)));
      }
    } catch (Exception e) {
      System.out.println("Failed to parse: ");
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

  public void saveVariable(Decl decl) {
    var ident = decl.children.get(0);
    var expr = decl.children.get(1);
    variables.put(ident, expr);
  }

  public void printVariables() {
    System.out.println();

    for (Identificator ident : variables.keySet()) 
      System.out.print(ident.val + " = " + Evaluator.evaluate(variables.get(ident)) + " ");
  }
}
