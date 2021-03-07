import java.util.Iterator;

/*
    Syntax definition:
    expr = operand ([+-] expr)?
    operand = multiplier ([/^*] operand)?
    multiplier = number | (expr)
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
        return str.charAt(pos++);
    }

    public Character next(int step) {
        Character c = str.charAt(pos);
        pos += step;
        return c;
    }

    public Character peek() {
        if (pos == str.length()) return '\0';
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

class Multiplier {
    private float val = 0.f;
    private Expr expr = null;

    Multiplier(CharIterator charIt) throws FailedToParseException {
        if (charIt.peek() == '(') {
            charIt.next();

            expr = new Expr(charIt);

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
    }

    float evaluate() {
        if (expr == null) return val;
        else return expr.evaluate();
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

    float evaluate() {
        float left = multiplier.evaluate();

        if (operation == '*') return left * operand.evaluate();
        else if (operation == '/') return left / operand.evaluate();
        else if (operation == '^') return (float) Math.pow((double) left, (double) operand.evaluate());
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

    float evaluate() {
        float left = operand.evaluate();

        if (operation == '+') return left + expr.evaluate();
        else if (operation == '-') return left - expr.evaluate();
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

class Main {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Please enter an arithmetic expression");
                return;
            }
            CharIterator expr = new CharIterator(args[0].replace(" ", ""));

            // System.out.println(new Expr(expr).evaluate());
            System.out.println(Expr.evaluate(expr));
        } catch (Exception e) {
            System.out.println("Failed to parse: ");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}