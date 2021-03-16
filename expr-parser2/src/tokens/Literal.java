package tokens;

import common.*;
import java.util.regex.*;

public sealed class Literal extends Token 
  permits Literal.Num 
{ 
  static final Pattern digit = Pattern.compile("[0-9]");
  
  Literal() {}

  Literal(CharIterator it) throws FailedToTokenizeException {
    children.add(new Num(it));
  }  

  public final class Num extends Literal {
    public double val;

    Num(double val) {
      this.val = val;
    }
    
    Num(CharIterator it) throws FailedToTokenizeException {
      if (digit.matcher(it.peek().toString()).matches()) {
        String strVal = "";

        while (digit.matcher(it.peek().toString()).matches())
          strVal += it.next();
            
        if (it.peek() == '.') {
          strVal += it.next();

          while (digit.matcher(it.peek().toString()).matches())
            strVal += it.next();
        }
        
        val = Double.parseDouble(strVal);
      } else throw new FailedToTokenizeException();
    }
  }
}