package tokens;

import common.*;
import java.util.*;
import java.util.concurrent.*;
import main.Main;

public sealed class Punct extends Token
  permits Punct.Bracket, Punct.Brace, Punct.Parenthesis, Punct.AngleBracket, Punct.Comma, Punct.Period, Punct.Semicolon, Punct.Colon
{ 
  Punct() {}

  public Punct(CharIterator it) throws FailedToTokenizeException {
    var list = new ArrayList<Callable<AbstractSyntaxTree>>();

    list.add(() -> { new AngleBracket((CharIterator)it.clone()); return new AngleBracket(it).children.get(0); });
    list.add(() -> { new Parenthesis((CharIterator)it.clone()); return new Parenthesis(it).children.get(0); });
    list.add(() -> { new Bracket((CharIterator)it.clone()); return new Bracket(it).children.get(0); });
    list.add(() -> { new Brace((CharIterator)it.clone()); return new Brace(it).children.get(0); });
    list.add(() -> { new Semicolon((CharIterator)it.clone()); return new Semicolon(it); });
    list.add(() -> { new Period((CharIterator)it.clone()); return new Period(it); });
    list.add(() -> { new Comma((CharIterator)it.clone()); return new Comma(it); });
    list.add(() -> { new Colon((CharIterator)it.clone()); return new Colon(it); });

    try {
      children.add(Main.executorService.invokeAny(list));
    } catch (InterruptedException | ExecutionException e) { throw new FailedToTokenizeException(); }
  }

  public static sealed class Parenthesis extends Punct
    permits Parenthesis.Left, Parenthesis.Right
  {
    Parenthesis() {}
  
    public Parenthesis(CharIterator it) throws FailedToTokenizeException {
      try {
        new Left((CharIterator)it.clone()); 
  
        children.add(new Left(it));
      } catch (Exception e) {
        new Right((CharIterator)it.clone()); 
  
        children.add(new Right(it));
      }
    }
  
    public static final class Left extends Parenthesis {
      Left(CharIterator it) throws FailedToTokenizeException {
        if (it.peek() == '(') it.next();
        else throw new FailedToTokenizeException("Expected opening parenthesis");
      }
    }

    public static final class Right extends Parenthesis {
      Right(CharIterator it) throws FailedToTokenizeException {
        if (it.peek() == ')') it.next();
        else throw new FailedToTokenizeException("Expected closing parenthesis");
      }
    }
  }
  
  public static sealed class Brace extends Punct
    permits Brace.Left, Brace.Right
  {
    Brace() {}
  
    public Brace(CharIterator it) throws FailedToTokenizeException {
      try {
        new Left((CharIterator)it.clone()); 
  
        children.add(new Left(it));
      } catch (Exception e) {
        new Right((CharIterator)it.clone()); 
  
        children.add(new Right(it));
      }
    }
  
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
  
  public static sealed class Bracket extends Punct
    permits Bracket.Left, Bracket.Right
  {
    Bracket() {}
  
    public Bracket(CharIterator it) throws FailedToTokenizeException {
      try {
        new Left((CharIterator)it.clone()); 
  
        children.add(new Left(it));
      } catch (Exception e) {
        new Right((CharIterator)it.clone()); 
  
        children.add(new Right(it));
      }
    }
  
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
  
  public static sealed class AngleBracket extends Punct
    permits AngleBracket.Left, AngleBracket.Right
  {
    AngleBracket() {}
  
    public AngleBracket(CharIterator it) throws FailedToTokenizeException {
      try {
        new Left((CharIterator)it.clone()); 
  
        children.add(new Left(it));
      } catch (Exception e) {
        new Right((CharIterator)it.clone()); 
  
        children.add(new Right(it));
      }
    }
  
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
  
  public static final class Comma extends Punct {
    public Comma(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == ',') it.next();
      else throw new FailedToTokenizeException();
    }
  } 
  
  public static final class Period extends Punct {
    public Period(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '.') it.next();
      else throw new FailedToTokenizeException();
    }
  } 
  
  public static final class Semicolon extends Punct {
    public Semicolon(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == ';') it.next();
      else throw new FailedToTokenizeException();
    }
  } 
  
  public static final class Colon extends Punct {
    public Colon(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == ':') it.next();
      else throw new FailedToTokenizeException();
    }
  }
} 