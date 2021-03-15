package expr-parser2.tokens;

import expr-parser2.Token;

public sealed abstract class Keyword extends Token 
  permits Quit, InteractiveMode 
{ }

public final class Quit extends Keyword {

}
