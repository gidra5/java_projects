package expr-parser2.tokens;

import expr-parser2.Token;

public sealed abstract class Operator extends Token
  permits Sub, Add, Mult, Div, Pow, Mod, Equal
{ }