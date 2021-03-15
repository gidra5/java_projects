package expr_parser2.tokens;

import expr_parser2.Token;

public sealed class Literal extends Token 
  permits Num 
{ }

public final class Num extends Literal {
  static final Pattern num = Pattern.compile("[0-9]");
  double val;

  Num(double val) {
    this.val = val;
  }
  
  Num(CharIterator it) throws FailedToTokenizeException {
    if (digit.matcher(it.peek().toString()).matches()) {
      String strVal = "";

      while (digit.matcher(charIt.peek().toString()).matches())
        strVal += charIt.next();
          
      if (charIt.peek() == '.') {
        strVal += charIt.next();

        while (digit.matcher(charIt.peek().toString()).matches())
          strVal += charIt.next();
      }
      
      val = Float.parseFloat(strVal);
    } else throw new FailedToTokenizeException();
  }
}