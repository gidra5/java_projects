package expr-parser2.tokens;

import expr-parser2.Token;

public sealed interface Literal extends Token 
  permits Num 
{

}

public final record Num(double val) implements Literal {
  
}
