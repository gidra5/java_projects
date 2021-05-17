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

  Identifier, 

  EOL, 
  EOT,
  Skip, 
  None
}
