package expr_parser2.tokens;

import expr_parser2.Token;

public sealed class Punct extends Token
  permits Bracket, Brace, Parenthesis, AngleBracket, Comma, Period, Semicolon, Colon
{ }

public sealed class Parenthesis extends Punct
  permits Parenthesis.Left, Parenthesis.Right
{
  public final class Left extends Parenthesis {
    Left(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '(') it.next();
      else throw new FailedToTokenizeException("Expected opening parenthesis");
    }
  }
  public final class Right extends Parenthesis {
    Right(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == ')') it.next();
      else throw new FailedToTokenizeException("Expected closing parenthesis");
    }
  }
}

public sealed class Brace extends Punct
  permits Brace.Left, Brace.Right
{
  public final class Left extends Brace {
    Left(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '[') it.next();
      else throw new FailedToTokenizeException("Expected opening parenthesis");
    }
  }
  public final class Right extends Brace {
    Right(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == ']') it.next();
      else throw new FailedToTokenizeException("Expected closing parenthesis");
    }
  }
}

public sealed class Bracket extends Punct
  permits Bracket.Left, Bracket.Right
{
  public final class Left extends Bracket {
    Left(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '{') it.next();
      else throw new FailedToTokenizeException("Expected opening parenthesis");
    }
  }
  public final class Right extends Bracket {
    Right(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '}') it.next();
      else throw new FailedToTokenizeException("Expected closing parenthesis");
    }
  }
}

public sealed class AngleBracket extends Punct
  permits AngleBracket.Left, AngleBracket.Right
{
  public final class Left extends AngleBracket {
    Left(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '<') it.next();
      else throw new FailedToTokenizeException("Expected opening parenthesis");
    }
  }
  public final class Right extends AngleBracket {
    Right(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '>') it.next();
      else throw new FailedToTokenizeException("Expected closing parenthesis");
    }
  }
}