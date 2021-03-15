package expr-parser2.tokens;

import expr-parser2.Token;

public sealed interface Punct extends Token
  permits Bracket, Brace, Parenthesis, AngleBracket, Comma, Period, Semicolon, Colon
{ }

public sealed interface Parenthesis extends Punct
  permits Parenthesis.Left, Parenthesis.Right
{
  public final class Left implements Parenthesis {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '(') { it.next(); return new Left(); }
      else throw new FailedToTokenizeException("Expected opening parenthesis");
    }
  }
  public final class Right implements Parenthesis {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == ')') { it.next(); return new Right(); }
      else throw new FailedToTokenizeException("Expected closing parenthesis");
    }
  }
}


public sealed interface Brace extends Punct
  permits Brace.Left, Brace.Right
{
  public final class Left implements Brace {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '[') { it.next(); return new Left(); }
      else throw new FailedToTokenizeException("Expected opening parenthesis");
    }
  }
  public final class Right implements Brace {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == ']') { it.next(); return new Right(); }
      else throw new FailedToTokenizeException("Expected closing parenthesis");
    }
  }
}

public sealed interface Bracket extends Punct
  permits Bracket.Left, Bracket.Right
{
  public final class Left implements Bracket {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '{') { it.next(); return new Left(); }
      else throw new FailedToTokenizeException("Expected opening parenthesis");
    }
  }
  public final class Right implements Bracket {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '}') { it.next(); return new Right(); }
      else throw new FailedToTokenizeException("Expected closing parenthesis");
    }
  }
}

public sealed interface AngleBracket extends Punct
  permits AngleBracket.Left, AngleBracket.Right
{
  public final class Left implements AngleBracket {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '<') { it.next(); return new Left(); }
      else throw new FailedToTokenizeException("Expected opening parenthesis");
    }
  }
  public final class Right implements AngleBracket {
    public Token tokenize(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '>') { it.next(); return new Right(); }
      else throw new FailedToTokenizeException("Expected closing parenthesis");
    }
  }
}