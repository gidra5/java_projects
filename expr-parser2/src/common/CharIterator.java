package common;

import java.util.*;

public class CharIterator implements Iterator<Character>, Cloneable {
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

  public boolean check(String str) {
    // TODO implement 
    return false;
  }

  public void setPos(int p) {
    if (p >= str.length()) throw new ArrayIndexOutOfBoundsException(p);
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

