package expr-parser2.tokens;

import expr-parser2.Token;

public sealed abstract class Literal extends Token 
  permits Num 
{ }

public final class Num extends Literal {
  double val;

  Num(double val) {
    this.val = val;
  }
}
