package expr-parser2.tokens;

import expr-parser2.Token;

public sealed interface Keyword extends Token 
  permits Quit 
{

}
