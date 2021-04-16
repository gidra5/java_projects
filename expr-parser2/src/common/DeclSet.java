package common;

import java.util.*;

import tokens.AbstractSyntaxTree;
import tokens.Token;

public class DeclSet extends HashSet<AbstractSyntaxTree.Decl> {
  private static final long serialVersionUID = 1L;

  public boolean add(AbstractSyntaxTree.Decl d) {
    if (super.contains(d)) super.remove(d);

    return super.add(d);
  }

  public AbstractSyntaxTree.Decl get(Token.Identifier id) {
    try {
      return this.parallelStream()
        .filter(x -> x.children.get(0).equals(id))
        .findFirst()
        .get();
    } catch (NoSuchElementException e) {
      System.out.println("No declaration for identifier " + id.val);
      e.printStackTrace();
      return null;
    }
  }
}
