package tokens;

import common.*;
import java.util.regex.*;
// import java.util.*;
// import java.util.concurrent.*;
// import main.Main;

public sealed class Token extends AbstractSyntaxTree
  permits Operator, Literal, Keyword, Punct, Token.Identifier, Token.EOL, Token.EOT
{

  Token() {}

  // fixed nesting but still not perfect

  public Token(CharIterator it) throws FailedToTokenizeException {
    // since order matters this parallelism fails
    // var list = new ArrayList<Callable<AbstractSyntaxTree>>();

    // list.add(() -> { new Operator((CharIterator)it.clone()); return new Operator(it).children.get(0); });
    // list.add(() -> { new Literal((CharIterator)it.clone()); return new Literal(it).children.get(0); });
    // list.add(() -> { new Keyword((CharIterator)it.clone()); return new Keyword(it).children.get(0); });
    // list.add(() -> { new Punct((CharIterator)it.clone()); return new Punct(it).children.get(0); });
    // list.add(() -> { new Identifier((CharIterator)it.clone()); return new Identifier(it); });
    // list.add(() -> { new EOL((CharIterator)it.clone()); return new EOL(it); });
    // list.add(() -> { new EOT((CharIterator)it.clone()); return new EOT(it); });

    // try {
    //   children.add(Main.executorService.invokeAny(list));
    // } catch (InterruptedException | ExecutionException e) {
    //   e.getCause().printStackTrace();
    //   throw new FailedToTokenizeException("Cannot tokenize string. " + e.getCause().getMessage());
    // }

    try {
      new Keyword((CharIterator)it.clone());

      children.add(new Keyword(it).children.get(0));
      return;
    } catch (Exception e) {}

    try {
      new Literal((CharIterator)it.clone());

      children.add(new Literal(it).children.get(0));
      return;
    } catch (Exception e) {}

    try {
      new Identifier((CharIterator)it.clone());

      children.add(new Identifier(it));
      return;
    } catch (Exception e) {}

    try {
      new Operator((CharIterator)it.clone());

      children.add(new Operator(it).children.get(0));
      return;
    } catch (Exception e) {}

    try {
      new Punct((CharIterator)it.clone());

      children.add(new Punct(it).children.get(0));
      return;
    } catch (Exception e) {}

    try {
      new EOL((CharIterator)it.clone());

      children.add(new EOL(it));
      return;
    } catch (Exception e) {}

    children.add(new EOT(it));
  }

  public static Token parse(CharIterator it) throws FailedToTokenizeException {
    // if (it.peek() instanceof Keyword.Quit)
    //   return it.next();

    // try {
    //   return new Decl(it);
    // } catch (Exception e) {  }

    // return new Expr(it);
    return (Token)(new Token(it).children.get(0));
  }

  public static final class EOL extends Token {
    public EOL(CharIterator it) throws FailedToTokenizeException {
      if (!(it.check("\n") || it.check(";"))) throw new FailedToTokenizeException();
    }

    public String toString() {
      return "\n";
    }
  }

  public static final class EOT extends Token {
    public EOT() {}

    public EOT(CharIterator it) throws FailedToTokenizeException {
      if (!it.check("")) throw new FailedToTokenizeException();
    }

    public String toString() {
      return "";
    }
  }

  public static final class Identifier extends Token {
    static final Pattern alphanum = Pattern.compile("[0-9a-zA-Z_]");
    public String val = "";

    public Identifier(String val) {
      this.val = val;
    }

    public Identifier(CharIterator it) throws FailedToTokenizeException {
      it.check("\\");

      if (Pattern.matches("[a-zA-Z_]", it.peek().toString())) {
        val += it.next();

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

    public String toString() {
      return val;
    }
  }
}
