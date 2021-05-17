package common;

import tokens.Token;
import tokens.TokenType;
import tokens.FailedToParseException;
import tokens.FailedToTokenizeException;

import java.util.*;

public class TokenIterator implements Iterator<Token>, Cloneable {
  private ArrayList<Token> tokenList = new ArrayList<>();
  private int pos = 0;

  public TokenIterator(ArrayList<Token> tokenList) {
    this.tokenList = tokenList;
  }

  public TokenIterator(String str) {
    var charIt = new CharIterator(str); 
    var token = Token.tokenize(charIt);

    while (token.type() != TokenType.EOT) {
      if (token.type() == TokenType.None) {
        System.out.println("Failed to tokenize at pos " + charIt.pos());
        break;
      }
      if (token.type() != TokenType.Skip)
        this.tokenList.add(token);

      token = Token.tokenize(charIt);
    }
  }

  public boolean hasNext() {
    return pos < tokenList.size();
  }

  public Token next() {
    return hasNext() ? tokenList.get(pos++) : new Token(TokenType.EOT);
  }

  public ArrayList<Token> next(int size) {
    var list = new ArrayList<Token>();

    if (pos + size >= tokenList.size()) {
      pos = tokenList.size();
      list.addAll(tokenList.subList(pos, tokenList.size()));
    }
    else {
      pos += size;
      list.addAll(tokenList.subList(pos, pos + size));
    }
    
    return list;
  }

  public Token peek() {
    return hasNext() ? tokenList.get(pos) : new Token(TokenType.EOT);
  }

  public ArrayList<Token> peek(int size) {
    var list = new ArrayList<Token>();

    if (pos + size >= tokenList.size())
      list.addAll(tokenList.subList(pos, tokenList.size()));
    else {
      list.addAll(tokenList.subList(pos, pos + size));
    }

    return list;
  }
  // public boolean check(ArrayList<Token> seq) {
  //   var sublist = peek(seq.size());

  //   if (!(sublist.size() == seq.size() && IntStream.range(0, seq.size()).allMatch(i -> seq.get(i).equals(sublist.get(i)))))
  //     return false;

  //   pos += seq.size();

  //   return true;
  // }

  public Token check(TokenType type) {
    if (peek().type() == type) return next();
    else return null;
  }

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
