package tokens;

import java.util.*;
import java.util.concurrent.*;

import common.*;
import main.Main;

public sealed class Keyword extends Token
  permits Keyword.Quit, Keyword.InteractiveMode, Keyword.Derivative
{
  public Keyword() {}

  public Keyword(CharIterator it) throws FailedToTokenizeException {
    var list = new ArrayList<Callable<AbstractSyntaxTree>>();

    list.add(() -> { new Quit((CharIterator)it.clone()); return new Quit(it); });
    list.add(() -> { new InteractiveMode((CharIterator)it.clone()); return new InteractiveMode(it); });
    list.add(() -> { new Derivative((CharIterator)it.clone()); return new Derivative(it); });

    try {
      children.add(Main.executorService.invokeAny(list));
    } catch (InterruptedException | ExecutionException e) { throw new FailedToTokenizeException(); }
  }

  public final class Derivative extends Keyword {
    public Derivative(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == 'd')
        it.next();
      else
        throw new FailedToTokenizeException();
    }
  }

  public final class InteractiveMode extends Keyword {
    public InteractiveMode(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == 'i') it.next();
      else throw new FailedToTokenizeException();
    }
  }

  public final class Quit extends Keyword {
    public Quit(CharIterator it) throws FailedToTokenizeException {
      if (it.peek() == 'q') it.next();
      else throw new FailedToTokenizeException();
    }
  }
}
