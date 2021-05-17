package common;

import java.util.*;

import tokens.AbstractSyntaxTree;
import tokens.Token;

public class DeclSet extends HashSet<AbstractSyntaxTree.DeclNode> {
  private static final long serialVersionUID = 1L;

  public boolean add(AbstractSyntaxTree.DeclNode d) {
    if (super.contains(d)) super.remove(d);

    return super.add(d);
  }

  public AbstractSyntaxTree.DeclNode get(Token id) {
    try {
      return this.parallelStream()
        .filter(x -> x.ident.equals(id))
        .findFirst()
        .get();
    } catch (NoSuchElementException e) {
      System.out.println("No declaration for identifier " + id.src());
      e.printStackTrace();
      return null;
    }
  }
}
