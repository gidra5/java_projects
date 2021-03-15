package expr_parser2.tokens;

import expr_parser2.Token;

public sealed class Keyword extends Token 
  permits Quit, InteractiveMode 
{ }

public final class Quit extends Keyword {
  Quit(CharIterator it) throws FailedToTokenizeException {
    if (it.peek() == 'q') it.next();
    else throw new FailedToTokenizeException();
  }
}

public final class InteractiveMode extends Keyword {
  InteractiveMode(CharIterator it) throws FailedToTokenizeException {
    if (it.peek() == 'i') it.next();
    else throw new FailedToTokenizeException();
  }
}