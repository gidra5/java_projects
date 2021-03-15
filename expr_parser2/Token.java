package expr_parser2;

public sealed class Token extends AbstractSyntaxTree
  permits Operator, Literal, Identifier, Keyword, Punct, EOL, EOT
{ 
  // todo fix nesting hell somehow

  Token(CharIterator it) throws FailedToTokenizeException {
    try {
      new Keyword((CharIterator)it.clone()); 

      children.add(new Keyword(it));
    } catch (Exception e) {
      try {
        new Literal((CharIterator)it.clone()); 

        children.add(new Literal(it));
      } catch (Exception e) {
        try {
          new Identifier((CharIterator)it.clone()); 

          children.add(new Identifier(it));
        } catch (Exception e) {
          try {
            new Operator((CharIterator)it.clone()); 

            children.add(new Operator(it));
          } catch (Exception e) {
            try {
              new Punct((CharIterator)it.clone()); 

              children.add(new Punct(it));
            } catch (Exception e) {
              try {
                new EOL((CharIterator)it.clone()); 

                children.add(new EOL(it));
              } catch (Exception e) {
                new EOT((CharIterator)it.clone()); 

                children.add(new EOT(it));
              }
            }
          }
        }
      }
    }
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

  Identifier(CharIterator it) throws FailedToTokenizeException {
    if (Pattern.matches("[a-zA-Z]", it.peek().toString())) {
      while (alphanum.matcher(it.peek().toString()).matches())
        val += it.next();
    } else throw new FailedToTokenizeException();
  }
}

public final class EOL extends Token {
  EOL(CharIterator it) throws FailedToTokenizeException {
    if (it.peek() == '\n' || it.peek() == ';') it.next();
    else throw new FailedToTokenizeException();
  }
}

public final class EOT extends Token {
  EOT(CharIterator it) throws FailedToTokenizeException {
    if (it.peek() == '\0') it.next();
    else throw new FailedToTokenizeException();
  }
}