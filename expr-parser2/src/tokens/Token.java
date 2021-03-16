package tokens;

import common.*;
import java.util.regex.*;

public sealed class Token extends AbstractSyntaxTree
  permits Operator, Literal, Keyword, Punct, Token.Identifier, Token.EOL, Token.EOT
{ 

  Token() {}

  // todo fix nesting hell somehow

  public Token(CharIterator it) throws FailedToTokenizeException {
    try {
      new Keyword((CharIterator)it.clone()); 

      children.add(new Keyword(it).children.get(0));
    } catch (Exception e) {
      try {
        new Literal((CharIterator)it.clone()); 

        children.add(new Literal(it).children.get(0));
      } catch (Exception e2) {
        try {
          new Identifier((CharIterator)it.clone()); 

          children.add(new Identifier(it));
        } catch (Exception e3) {
          try {
            new Operator((CharIterator)it.clone()); 

            children.add(new Operator(it).children.get(0));
          } catch (Exception e4) {
            try {
              new Punct((CharIterator)it.clone()); 

              children.add(new Punct(it).children.get(0));
            } catch (Exception e5) {
              try {
                new EOL((CharIterator)it.clone()); 

                children.add(new EOL(it));
              } catch (Exception e6) {
                new EOT((CharIterator)it.clone()); 

                children.add(new EOT(it));
              }
            }
          }
        }
      }
    }
  }

  public static final class EOL extends Token {
    public EOL(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '\n' || it.peek() == ';') it.next();
      else throw new FailedToTokenizeException();
    }
  }

  public static final class EOT extends Token {
    public EOT() {}
  
    public EOT(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '\0') it.next();
      else throw new FailedToTokenizeException();
    }
  }

  public static final class Identifier extends Token {
    static final Pattern alphanum = Pattern.compile("[0-9a-zA-Z_]");
    public String val = "";
  
    public Identifier(String val) {
      this.val = val;
    }
  
    public Identifier(CharIterator it) throws FailedToTokenizeException {
      if (Pattern.matches("[a-zA-Z]", it.peek().toString())) {
        while (alphanum.matcher(it.peek().toString()).matches())
          val += it.next();
      } else throw new FailedToTokenizeException();
    }

    public boolean equals(Object obj) {
      if (obj instanceof Token.Identifier id) {
        return id.val.equals(this.val);
      } else return false;
    }

    public int hashCode() {
      return this.val.hashCode();
    }
  }
}
