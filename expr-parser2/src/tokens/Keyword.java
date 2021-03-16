package tokens;

import common.*;

public sealed class Keyword extends Token 
  permits Keyword.Quit, Keyword.InteractiveMode 
{ 
  public Keyword() {}

  public Keyword(CharIterator it) throws FailedToTokenizeException {
    try {
      new Quit((CharIterator)it.clone()); 

      children.add(new Quit(it));
    } catch (Exception e) {
      new InteractiveMode((CharIterator)it.clone()); 

      children.add(new InteractiveMode(it));
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
