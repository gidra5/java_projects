package tokens;

import common.*;
import java.util.concurrent.*;
import java.util.*;
import main.Main;

public sealed class Operator extends Token
  permits Operator.Sub, Operator.Add, Operator.Mult, Operator.Div, Operator.Pow, Operator.Mod, Operator.Equal
{
  Operator() {}

  public Operator(CharIterator it) throws FailedToTokenizeException {
    var list = new ArrayList<Callable<AbstractSyntaxTree>>();

    list.add(() -> { new Sub((CharIterator)it.clone()); return new Sub(it); });
    list.add(() -> { new Add((CharIterator)it.clone()); return new Add(it); });
    list.add(() -> { new Mult((CharIterator)it.clone()); return new Mult(it); });
    list.add(() -> { new Div((CharIterator)it.clone()); return new Div(it); });
    list.add(() -> { new Pow((CharIterator)it.clone()); return new Pow(it); });
    list.add(() -> { new Mod((CharIterator)it.clone()); return new Mod(it); });
    list.add(() -> { new Equal((CharIterator)it.clone()); return new Equal(it); });

    try {
      children.add(Main.executorService.invokeAny(list));
    } catch (InterruptedException | ExecutionException e) { throw new FailedToTokenizeException(); }
  }

  public static final class Sub extends Operator {
    public Sub() {}
    public Sub(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '-') it.next();
      else throw new FailedToTokenizeException();
    }
  }

  public static final class Add extends Operator {
    public Add() {}
    public Add(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '+') it.next();
      else throw new FailedToTokenizeException();
    }
  }

  public static final class Mult extends Operator {
    public Mult() {}
    public Mult(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '*') it.next();
      else throw new FailedToTokenizeException();
    }
  }

  public static final class Div extends Operator {
    public Div() {}
    public Div(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '/') it.next();
      else throw new FailedToTokenizeException();
    }
  }

  public static final class Pow extends Operator {
    public Pow() {}
    public Pow(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == '^') it.next();
      else throw new FailedToTokenizeException();
    }
  }

  public static final class Mod extends Operator {
    public Mod() {}
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