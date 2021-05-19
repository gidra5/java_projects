package tokens;

public enum TokenType {
  Sub, 
  Add, 
  Mult, 
  Div, 
  Pow, 
  Mod, 
  Factorial, 
  Equal,

  Number,

  Quit, 
  Let, 
  InteractiveMode, 
  Derivative,
  Simplify,

  LBracket, 
  LBrace, 
  LParenthesis, 
  LAngleBracket, 
  RBracket, 
  RBrace, 
  RParenthesis, 
  RAngleBracket, 
  Comma, 
  Period, 
  Semicolon, 
  Colon,
  Placeholder,

  Identifier, 

  EOL, 
  EOT,
  Skip, 
  None
}
