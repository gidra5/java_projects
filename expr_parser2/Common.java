package expr_parser2;
import expr_parser2.*;

import java.util.*;

class CharIterator implements Iterator<Character>, Cloneable {
  private final String str;
  private int pos = 0;

  public CharIterator(String str) {
    this.str = str;
  }

  public boolean hasNext() {
    return pos < str.length();
  }

  public Character next() {
    if (pos >= str.length()) return '\0';
    return str.charAt(pos++);
  }

  public Character next(int step) {
    if (pos + step >= str.length()) return '\0';
    else {
      pos += step;
      return str.charAt(pos++);
    }
  }

  public Character peek() {
    if (pos >= str.length()) return '\0';
    else return str.charAt(pos);
  }

  public Character peek(int step) {
    if (pos + step >= str.length()) return '\0';
    else return str.charAt(pos + step);
  }

  public void setPos(int p) {
    if (p >= str.length()) throw new ArrayIndexOutOfBoundsException(p);
    else pos = p;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }

  public Object clone() {
    return super.clone();
  }
}

class TokenIterator implements Iterator<Token>, Cloneable {
  private final ArrayList<Token> tokenList;
  private int pos = 0;

  public CharIterator(ArrayList<Token> tokenList) {
    this.tokenList = tokenList;
  }

  public boolean hasNext() {
    return pos < tokenList.size();
  }

  public Token next() {
    if (pos >= tokenList.size()) return '\0';
    else return tokenList.get(pos++);
  }

  public Token next(int step) {
    if (pos >= tokenList.size()) return '\0';
    else {
      Character c = tokenList.get(pos);
      pos += step;
      return c;
    }
  }

  public Token peek() {
    if (pos >= tokenList.size()) return '\0';
    else return tokenList.get(pos);
  }

  public Token peek(int step) {
    if (pos + step >= tokenList.size()) return '\0';
    else return tokenList.get(pos + step);
  }

  public void setPos(int p) {
    if (p >= tokenList.size()) throw new ArrayIndexOutOfBoundsException(p);
    else pos = p;
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }

  public Object clone() {
    return super.clone();
  }
}

class Evaluator {
  static double evaluate(AbstractSyntaxTree ast) {

    return 0.;
  } 
}