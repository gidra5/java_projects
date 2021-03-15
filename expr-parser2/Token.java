package expr-parser2;

public sealed abstract class Token extends AbstractSyntaxTree
  permits Operator, Literal, Identifier, Keyword, Punct, EOL, EOT
{
  public abstract Token tokenize(CharIterator it) throws FailedToTokenizeException;

  public AbstractSyntaxTree parse(TokenIterator it) {
    return it.next();
  }
}

class FailedToTokenizeException extends Exception {
  private static final long serialVersionUID = 1L;

  FailedToTokenizeException(String errmsg) {
      super(errmsg);
  }
}

public final class Identifier extends Token {
  static final Pattern alphanum = Pattern.compile("[0-9a-zA-Z_]");
  public String val;

  Identifier(String val) {
    this.val = val;
  }

  public Token tokenize(CharIterator it) throws FailedToTokenizeException {
    String id = "";

    if (Pattern.matches("[a-zA-Z]", it.peek().toString())) {
      while (alphanum.matcher(it.peek().toString()).matches())
        id += it.next();
    } else throw new FailedToTokenizeException("Expected identifier starting from letter");

    return new Identifier(id);
  }
}

public final class EOL extends Token {
  public Token tokenize(CharIterator it) throws FailedToTokenizeException {
    if (it.peek() == '\n' || it.peek() == ';') { it.next(); return new EOL(); }
    else throw new FailedToTokenizeException("Expected EOL symbol");
  }
}

public final class EOT extends Token {
  public Token tokenize(CharIterator it) throws FailedToTokenizeException {
    if (it.peek() == '\0') { it.next(); return new EOT(); }
    else throw new FailedToTokenizeException("Expected EOT symbol");
  }
}