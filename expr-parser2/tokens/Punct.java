package expr-parser2.tokens;

import expr-parser2.Token;

public sealed abstract class Punct extends Token
  permits Bracket, Brace, Parenthesis, AngleBracket, Comma, Period, Semicolon, Colon
{ }

public sealed abstract class Parenthesis extends Punct
  permits Parenthesis.Left, Parenthesis.Right
{
  public final class Left extends Parenthesis {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '(') { it.next(); return new Left(); }
      else throw new FailedToTokenizeException("Expected opening parenthesis");
    }
  }
  public final class Right extends Parenthesis {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == ')') { it.next(); return new Right(); }
      else throw new FailedToTokenizeException("Expected closing parenthesis");
    }
  }
}


public sealed abstract class Brace extends Punct
  permits Brace.Left, Brace.Right
{
  public final class Left extends Brace {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '[') { it.next(); return new Left(); }
      else throw new FailedToTokenizeException("Expected opening parenthesis");
    }
  }
  public final class Right extends Brace {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == ']') { it.next(); return new Right(); }
      else throw new FailedToTokenizeException("Expected closing parenthesis");
    }
  }
}

public sealed abstract class Bracket extends Punct
  permits Bracket.Left, Bracket.Right
{
  public final class Left extends Bracket {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '{') { it.next(); return new Left(); }
      else throw new FailedToTokenizeException("Expected opening parenthesis");
    }
  }
  public final class Right extends Bracket {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '}') { it.next(); return new Right(); }
      else throw new FailedToTokenizeException("Expected closing parenthesis");
    }
  }
}

public sealed abstract class AngleBracket extends Punct
  permits AngleBracket.Left, AngleBracket.Right
{
  public final class Left extends AngleBracket {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '<') { it.next(); return new Left(); }
      else throw new FailedToTokenizeException("Expected opening parenthesis");
    }
  }
  public final class Right extends AngleBracket {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '>') { it.next(); return new Right(); }
      else throw new FailedToTokenizeException("Expected closing parenthesis");
    }
  }
}