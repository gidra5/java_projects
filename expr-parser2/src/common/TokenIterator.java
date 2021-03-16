package common;
import tokens.Token;

import java.util.*;

public class TokenIterator implements Iterator<Token>, Cloneable {
  private ArrayList<Token> tokenList;
  private int pos = 0;

  public TokenIterator(ArrayList<Token> tokenList) {
    this.tokenList = tokenList;
  }

  public boolean hasNext() {
    return pos < tokenList.size();
  }

  public Token next() {
    if (pos >= tokenList.size()) return new Token.EOT();
    else return tokenList.get(pos++);
  }

  public Token next(int step) {
    if (pos >= tokenList.size()) return new Token.EOT();
    else {
      Token c = tokenList.get(pos);
      pos += step;
      return c;
    }
  }

  public Token peek() {
    if (pos >= tokenList.size()) return new Token.EOT();
    else return tokenList.get(pos);
  }

  public Token peek(int step) {
    if (pos + step >= tokenList.size()) return new Token.EOT();
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
    try {
      return super.clone();
    } catch (Exception e) {
      return null;
    }
  }
}

