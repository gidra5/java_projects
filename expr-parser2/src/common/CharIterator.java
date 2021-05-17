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
    if (!hasNext()) return '\0';
    return str.charAt(pos++);
  }

  public String next(int size) {
    if (!hasNext()) return "";
    else if (pos + size >= str.length())
      return str.substring(pos);
    else {
      pos += size;
      return str.substring(pos - size, pos);
    }
  }

  public Character peek() {
    return hasNext() ? str.charAt(pos) : '\0';
  }

  public String peek(int size) {
    if (!hasNext()) return "";
    else if (pos + size >= str.length())
      return str.substring(pos);
    else
      return str.substring(pos, pos + size);
  }

  public boolean check(String str) {
    var substr = peek(str.length());

    if (!substr.equals(str))
      return false;

    pos += str.length();

    return true;
  }

  public void setPos(int p) {
    if (p >= str.length()) throw new ArrayIndexOutOfBoundsException(p);
    else pos = p;
  }

  public int pos() {
    return pos;
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
