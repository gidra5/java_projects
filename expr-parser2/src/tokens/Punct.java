package tokens;

import common.*;

public sealed class Punct extends Token
  permits Punct.Bracket, Punct.Brace, Punct.Parenthesis, Punct.AngleBracket, Punct.Comma, Punct.Period, Punct.Semicolon, Punct.Colon
{ 
  Punct() {}

  public Punct(CharIterator it) throws FailedToTokenizeException {
    try {
      new Bracket((CharIterator)it.clone()); 

      children.add(new Bracket(it));
    } catch (Exception e) {
      try {
        new Brace((CharIterator)it.clone()); 
  
        children.add(new Brace(it));
      } catch (Exception e2) {
        try {
          new Parenthesis((CharIterator)it.clone()); 
    
          children.add(new Parenthesis(it));
        } catch (Exception e3) {
          try {
            new AngleBracket((CharIterator)it.clone()); 
      
            children.add(new AngleBracket(it));
          } catch (Exception e4) {
            try {
              new Comma((CharIterator)it.clone()); 
        
              children.add(new Comma(it));
            } catch (Exception e5) {
              try {
                new Period((CharIterator)it.clone()); 
          
                children.add(new Period(it));
              } catch (Exception e6) {
                try {
                  new Semicolon((CharIterator)it.clone()); 
            
                  children.add(new Semicolon(it));
                } catch (Exception e7) {
                  new Colon((CharIterator)it.clone()); 
            
                  children.add(new Colon(it));
                }
              }
            }
          }
        }
      }
    }
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