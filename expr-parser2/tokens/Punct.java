package expr-parser2.tokens;

import expr-parser2.Token;

public sealed interface Punct extends Token 
  permits Bracket, Brace, Parenthesis, AngleBracket, Comma, Period, Semicolon, Colon
{

}