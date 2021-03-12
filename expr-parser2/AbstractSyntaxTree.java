package expr-parser2;

public sealed interface AbstractSyntaxTree 
  permits Token, Expr, Product, Multiplier, Decl
{
  public ArrayList<AbstractSyntaxTree> children = null;

  public AbstractSyntaxTree parse(TokenIterator it);
  // todo methods...
}