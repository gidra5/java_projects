package common;

import tokens.Token;
import tokens.FailedToParseException;
import tokens.FailedToTokenizeException;

import java.util.*;

public class TokenIterator implements Iterator<Token>, Cloneable {
  private ArrayList<Token> tokenList = new ArrayList<>();
  private int pos = 0;

  public TokenIterator(ArrayList<Token> tokenList) {
    this.tokenList = tokenList;
  }

  public TokenIterator(String str) throws FailedToTokenizeException {
    var charIt = new CharIterator(str);

    while (charIt.peek() != '\0') {
      while (charIt.check(" ") || charIt.check("\t")) { }

      // this.tokenList.add((Token)(new Token(charIt).children.get(0)));
      this.tokenList.add(Token.parse(charIt));
    }
  }

  public boolean hasNext() {
    return pos < tokenList.size();
  }

  public Token next() {
    return hasNext() ? tokenList.get(pos++) : new Token.EOT();
  }

  public ArrayList<Token> next(int size) {
    if (pos >= tokenList.size())
      return new ArrayList<Token>();
    else if (pos + size >= tokenList.size())
      return (ArrayList<Token>) tokenList.subList(pos, tokenList.size());
    else {
      pos += size;
      return (ArrayList<Token>) tokenList.subList(pos - size, pos);
    }
  }

  public Token peek() {
    return hasNext() ? tokenList.get(pos) : new Token.EOT();
  }

  public ArrayList<Token> peek(int size) {
    if (pos >= tokenList.size())
      return new ArrayList<Token>();
    else if (pos + size >= tokenList.size())
      return (ArrayList<Token>) tokenList.subList(pos, tokenList.size());
    else {
      return (ArrayList<Token>) tokenList.subList(pos, pos + size);
    }
  }
  // public boolean check(ArrayList<Token> seq) {
  //   var sublist = peek(seq.size());

  //   if (!(sublist.size() == seq.size() && IntStream.range(0, seq.size()).allMatch(i -> seq.get(i).equals(sublist.get(i)))))
  //     return false;

  //   pos += seq.size();

  //   return true;
  // }

  public <T extends Token> T check(Class<T> c, String msgIfFailed) throws FailedToParseException {
    if (peek().getClass().getName() != c.getName())
      throw new FailedToParseException(msgIfFailed);

    return (T) next();
  }

  // public <T extends Token> boolean check(Class<T> c) {
  //   if (peek().getClass().getName() != c.getName())
  //     return false;

  //   ++pos;
  //   return true;
  // }

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
