package expr-parser2.tokens;

import expr-parser2.Token;

public sealed class Operator implements Token
  permits Sub, Add, Mult, Div, Pow, Mod, Equal
{

}