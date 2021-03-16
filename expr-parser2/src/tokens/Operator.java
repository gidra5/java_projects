package tokens;

import common.*;

public sealed class Operator extends Token
  permits Operator.Sub, Operator.Add, Operator.Mult, Operator.Div, Operator.Pow, Operator.Mod, Operator.Equal
{ 
  Operator() {}

  // todo fix nesting hell somehow

  public Operator(CharIterator it) throws FailedToTokenizeException {
    try {
      new Sub((CharIterator)it.clone()); 

      children.add(new Sub(it));
    } catch (Exception e) {
      try {
        new Add((CharIterator)it.clone()); 

        children.add(new Add(it));
      } catch (Exception e2) {
        try {
          new Mult((CharIterator)it.clone()); 

          children.add(new Mult(it));
        } catch (Exception e3) {
          try {
            new Div((CharIterator)it.clone()); 

            children.add(new Div(it));
          } catch (Exception e4) {
            try {
              new Pow((CharIterator)it.clone()); 

              children.add(new Pow(it));
            } catch (Exception e5) {
              try {
                new Mod((CharIterator)it.clone()); 

                children.add(new Mod(it));
              } catch (Exception e6) {
                new Equal((CharIterator)it.clone()); 

                children.add(new Equal(it));
              }
            }
          }
        }
      }
    }
  }
  
  public static final class Sub extends Operator {
    public Sub(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '-') it.next();
      else throw new FailedToTokenizeException();
    }
  }
  
  public static final class Add extends Operator {
    public Add(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '+') it.next();
      else throw new FailedToTokenizeException();
    }
  }
  
  public static final class Mult extends Operator {
    public Mult(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '*') it.next();
      else throw new FailedToTokenizeException();
    }
  }
  
  public static final class Div extends Operator {
    public Div(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '/') it.next();
      else throw new FailedToTokenizeException();
    }
  }
  
  public static final class Pow extends Operator {
    public Pow(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '^') it.next();
      else throw new FailedToTokenizeException();
    }
  }
  
  public static final class Mod extends Operator {
    public Mod(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '%') it.next();
      else throw new FailedToTokenizeException();
    }
  }
  
  public static final class Equal extends Operator {
    public Equal(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '=') it.next();
      else throw new FailedToTokenizeException();
    }

  }
}