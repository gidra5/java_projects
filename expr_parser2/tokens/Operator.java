package expr_parser2.tokens;

import expr_parser2.Token;

public sealed class Operator extends Token
  permits Sub, Add, Mult, Div, Pow, Mod, Equal
{ }

public final class Sub extends Operator {
  Sub(CharIterator it) throws FailedToTokenizeException {
    if (it.peek() == '-') it.next();
    else throw new FailedToTokenizeException();
  }
}

public final class Add extends Operator {
  Sub(CharIterator it) throws FailedToTokenizeException {
    if (it.peek() == '+') it.next();
    else throw new FailedToTokenizeException();
  }
}

public final class Mult extends Operator {
  Sub(CharIterator it) throws FailedToTokenizeException {
    if (it.peek() == '*') it.next();
    else throw new FailedToTokenizeException();
  }
}

public final class Div extends Operator {
  Sub(CharIterator it) throws FailedToTokenizeException {
    if (it.peek() == '/') it.next();
    else throw new FailedToTokenizeException();
  }
}

public final class Pow extends Operator {
  Sub(CharIterator it) throws FailedToTokenizeException {
    if (it.peek() == '^') it.next();
    else throw new FailedToTokenizeException();
  }
}

public final class Mod extends Operator {
  Sub(CharIterator it) throws FailedToTokenizeException {
    if (it.peek() == '%') it.next();
    else throw new FailedToTokenizeException();
  }
}

public final class Equal extends Operator {
  Equal(CharIterator it) throws FailedToTokenizeException {
    if (it.peek() == '=') it.next();
    else throw new FailedToTokenizeException();
  }
}