import java.util.*;
import java.util.regex.*;

/*
    Syntax definition:
    decl = ident = expr
    expr = summand ([+-] expr)?
    operand = multiplier ([/^*] summand)?
    multiplier = number | ident | (expr)

    enter expression to evaluate it or i to enter interactive mode
*/

class CharIterator implements Iterator<Character> {
    private final String str;
    private int pos = 0;

    public CharIterator(String str) {
        this.str = str;
    }

    public boolean hasNext() {
        return pos < str.length();
    }

    public Character next() {
        if (pos>= str.length()) return '\0';
        return str.charAt(pos++);
    }

    public Character next(int step) {
        Character c = str.charAt(pos);
        pos += step;
        return c;
    }

    public Character peek() {
        if (pos >= str.length()) return '\0';
        else return str.charAt(pos);
    }

    public Character peek(int step) {
        return str.charAt(pos + step);
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}

class FailedToParseException extends Exception {
    private static final long serialVersionUID = 1L;

    FailedToParseException(String errmsg) {
        super(errmsg);
    }
}

class Ident {
    String val = "";

    private Pattern alphanum = Pattern.compile("[0-9a-zA-Z_]");

    Ident(CharIterator charIt) throws FailedToParseException {
        if (Pattern.matches("[a-zA-Z]", charIt.peek().toString())) {
            while (alphanum.matcher(charIt.peek().toString()).matches())
                val += charIt.next();
        } else throw new FailedToParseException("Invaid symbol, please use here either number or brackets");
    }
}

class Multiplier {
    private float val = 0.f;
    private Expr expr = null;
    private Ident ident = null;

    private Pattern digit = Pattern.compile("[0-9]");

    Multiplier(CharIterator charIt) throws FailedToParseException {
        if (charIt.peek() == '(') {
            charIt.next();

            expr = new Expr(charIt);

            if (charIt.next() != ')') throw new FailedToParseException("Missing closing bracket");
        } 
        else if (digit.matcher(charIt.peek().toString()).matches()) {
            String str_val = "";

            while (digit.matcher(charIt.peek().toString()).matches())
                str_val += charIt.next();
                
            if (charIt.peek() == '.') {
                str_val += charIt.next();

                while (digit.matcher(charIt.peek().toString()).matches())
                    str_val += charIt.next();
            }
            
            val = Float.parseFloat(str_val);
        } 
        else if (Pattern.matches("[a-zA-Z]", charIt.peek().toString())) ident = new Ident(charIt); 
        else throw new FailedToParseException("Invaid symbol, please use here either number or brackets");
    }

    float evaluate() throws FailedToParseException {
        if (expr != null) return expr.evaluate();
        else if (ident != null) throw new FailedToParseException("Unexpected identifier");
        else return val;
    }

    float evaluate(HashMap<String, Expr> variables) throws FailedToParseException {
        if (expr != null) return expr.evaluate(variables);
        else if (ident != null) {
            if (variables.get(ident.val) != null) 
                return variables.get(ident.val).evaluate(variables);
            else throw new FailedToParseException("No declared identifier " + ident.val);
        }
        else return val;
    }

    static float evaluate(CharIterator charIt) throws FailedToParseException {
        float val = 0.f;

        if (charIt.peek() == '(') {
            charIt.next();

            val = Expr.evaluate(charIt);

            if (charIt.next() != ')') throw new FailedToParseException("Missing closing bracket");
        } else if ("0123456789".contains(charIt.peek().toString()))
        {
            while ("0123456789".contains(charIt.peek().toString()))
                val = 10 * val + Integer.parseInt(charIt.next().toString());

            if (charIt.peek() == '.') {
                charIt.next();
                int order = 1;

                while ("0123456789".contains(charIt.peek().toString())) {
                    val += Integer.parseInt(charIt.next().toString()) * Math.pow(10, -order);
                    ++order;
                }
            }
        } else throw new FailedToParseException("Invaid symbol, please use here either number or brackets");

        return val;
    }
}

class Operand {
    private Multiplier multiplier;
    private char operation;
    private Operand operand;

    Operand(CharIterator charIt) throws FailedToParseException {
        multiplier = new Multiplier(charIt);

        if ("*/^".contains(charIt.peek().toString())) {
            operation = charIt.next();

            operand = new Operand(charIt);
        }
    }

    float evaluate() throws FailedToParseException {
        float left = multiplier.evaluate();

        if (operation == '*') return left * operand.evaluate();
        else if (operation == '/') return left / operand.evaluate();
        else if (operation == '^') return (float) Math.pow((double) left, (double) operand.evaluate());
        else return left;
    }
    
    float evaluate(HashMap<String, Expr> variables) throws FailedToParseException {
        float left = multiplier.evaluate(variables);

        if (operation == '*') return left * operand.evaluate(variables);
        else if (operation == '/') return left / operand.evaluate(variables);
        else if (operation == '^') return (float) Math.pow((double) left, (double) operand.evaluate(variables));
        else return left;
    }

    static float evaluate(CharIterator charIt) throws FailedToParseException {
        float left = Multiplier.evaluate(charIt);
        char op = charIt.next();

        if (op == '*') return left * Operand.evaluate(charIt);
        else if (op == '/') return left / Operand.evaluate(charIt);
        else if (op == '^') return (float) Math.pow((double) left, (double) Operand.evaluate(charIt));
        else return left;
    }
}

class Expr {
    private Operand operand;
    private char operation;
    private Expr expr;

    Expr(CharIterator charIt) throws FailedToParseException {
        operand = new Operand(charIt);

        if ("+-".contains(charIt.peek().toString())) {
            operation = charIt.next();

            expr = new Expr(charIt);
        }
    }

    float evaluate() throws FailedToParseException {
        float left = operand.evaluate();

        if (operation == '+') return left + expr.evaluate();
        else if (operation == '-') return left - expr.evaluate();
        else return left;
    }

    float evaluate(HashMap<String, Expr> variables) throws FailedToParseException {
        float left = operand.evaluate(variables);

        if (operation == '+') return left + expr.evaluate(variables);
        else if (operation == '-') return left - expr.evaluate(variables);
        else return left;
    }

    static float evaluate(CharIterator charIt) throws FailedToParseException {
        float left = Operand.evaluate(charIt);
        char op = charIt.next();

        if (op == '+') return left + Expr.evaluate(charIt);
        else if (op == '-') return left - Expr.evaluate(charIt);
        else return left;
    }
}

class Decl {
    Ident ident;
    Expr expr;

    Decl(CharIterator charIt) throws FailedToParseException {
        ident = new Ident(charIt);

        if (charIt.next() != '=') throw new FailedToParseException("wtf is this");

        expr = new Expr(charIt);
    }
}

class Main {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Please enter an arithmetic expression as first argument or enter interactive mode");
                return;
            } else if (args[0].equals("i")) {
                Scanner scanner = new Scanner(System.in);
                HashMap<String, Expr> variables = new HashMap<String, Expr>();

                while (true) {
                    String next_line = scanner.nextLine().replace(" ", "");

                    if (next_line.equals("q")) break;

                    try {
                        CharIterator decl = new CharIterator(next_line);
                        Decl d = new Decl(decl);
                        
                        variables.put(d.ident.val, d.expr);

                        for (String ident : variables.keySet()) {
                            try {
                                System.out.print(ident + " = " + variables.get(ident).evaluate(variables) + " ");
                            } catch (FailedToParseException e2) {
                                System.out.print(e2.getMessage());
                            }
                        }
                        System.out.println();
                    } catch (FailedToParseException e) {
                        CharIterator expr = new CharIterator(next_line);
                        
                        try {
                            System.out.print(new Expr(expr).evaluate(variables));
                        } catch (FailedToParseException e2) {
                            System.out.println(e2.getMessage());
                        }
                    }
                }

                scanner.close();
            } else {
                CharIterator expr = new CharIterator(args[0].replace(" ", ""));
    
                System.out.println(new Expr(expr).evaluate());
                // System.out.println(Expr.evaluate(expr));
            }
        } catch (Exception e) {
            System.out.println("Failed to parse: ");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}