package tokens;

import common.*;
import java.util.regex.*;
import java.util.*;

public record Token(TokenType type, String src) {
  private static HashMap<String, TokenType> tokenTable = new HashMap<>();

  static {
    tokenTable.put("let", TokenType.Let);
    tokenTable.put("quit", TokenType.Quit);
    tokenTable.put("d", TokenType.Derivative);
    tokenTable.put("i", TokenType.InteractiveMode);
    
    tokenTable.put("-", TokenType.Sub);
    tokenTable.put("+", TokenType.Add);
    tokenTable.put("*", TokenType.Mult);
    tokenTable.put("/", TokenType.Div);
    tokenTable.put("^", TokenType.Pow);
    tokenTable.put("%", TokenType.Mod);
    tokenTable.put("=", TokenType.Equal);
    tokenTable.put("!", TokenType.Factorial);
    tokenTable.put("[", TokenType.LBracket);
    tokenTable.put("]", TokenType.RBracket);
    tokenTable.put("{", TokenType.LBrace);
    tokenTable.put("}", TokenType.RBrace);
    tokenTable.put("(", TokenType.LParenthesis);
    tokenTable.put(")", TokenType.RParenthesis);
    tokenTable.put("<", TokenType.LAngleBracket);
    tokenTable.put(">", TokenType.RAngleBracket);
    tokenTable.put(".", TokenType.Period);
    tokenTable.put(",", TokenType.Comma);
    tokenTable.put(":", TokenType.Colon);
    tokenTable.put(";", TokenType.Semicolon);
    tokenTable.put("\n", TokenType.EOL);
    tokenTable.put("\0", TokenType.EOT);
    tokenTable.put("\t", TokenType.Skip);
    tokenTable.put(" ", TokenType.Skip);
  }

  public Token() {
    this(TokenType.None);
  }
  
  public Token(TokenType type) {
    this(type, "");
  }

  private static Token tokenizeIdentifier(CharIterator it) {
    var src = "";
    var type = TokenType.None;

    if (Pattern.matches("[a-zA-Z_]", it.peek().toString())) {
      Pattern alphanum = Pattern.compile("[0-9a-zA-Z_]");
      type = TokenType.Identifier;
      src += it.next();

      while (alphanum.matcher(it.peek().toString()).matches())
        src += it.next();
    }

    return new Token(type, src);
  }
  
  private static Token tokenizeNumber(CharIterator it) {
    var src = "";
    var type = TokenType.None;

    final Pattern digit = Pattern.compile("[0-9]");
    if (digit.matcher(it.peek().toString()).matches()) {
      src = "";

      while (digit.matcher(it.peek().toString()).matches())
        src += it.next();

      if (it.peek() == '.') {
        src += it.next();

        while (digit.matcher(it.peek().toString()).matches())
          src += it.next();
      }

      type = TokenType.Number;
    }

    return new Token(type, src);
  }

  public static Token tokenize(CharIterator it) {
    var src = "";
    var type = TokenType.None;
    var ident = tokenizeIdentifier(it);

    src = ident.src();
    type = ident.type();

    if (type == TokenType.Identifier) {
      type = tokenTable.get(src);
      if (type == null) type = TokenType.Identifier;
      // type = switch (src) {
      //   case "let" -> TokenType.Let;
      //   case "quit" -> TokenType.Quit;
      //   case "d" -> TokenType.Derivative;
      //   case "i" -> TokenType.InteractiveMode;
      //   default -> TokenType.Identifier;
      // };
    } else {
      var number = tokenizeNumber(it);
  
      src = number.src();
      type = number.type();

      if (type == TokenType.None) {
        src = it.peek().toString();
        type = tokenTable.get(src);
        if (type == null) type = TokenType.None;
        else it.next();
        // src = it.peek().toString();

        // type = switch (src) {
        //   case "-" -> TokenType.Sub;
        //   case "+" -> TokenType.Add;
        //   case "*" -> TokenType.Mult;
        //   case "/" -> TokenType.Div;
        //   case "^" -> TokenType.Pow;
        //   case "%" -> TokenType.Mod;
        //   case "=" -> TokenType.Equal;
        //   case "!" -> TokenType.Factorial;
        //   case "[" -> TokenType.LBracket;
        //   case "]" -> TokenType.RBracket;
        //   case "{" -> TokenType.LBrace;
        //   case "}" -> TokenType.RBrace;
        //   case "(" -> TokenType.LParenthesis;
        //   case ")" -> TokenType.RParenthesis;
        //   case "<" -> TokenType.LAngleBracket;
        //   case ">" -> TokenType.RAngleBracket;
        //   case "." -> TokenType.Period;
        //   case "," -> TokenType.Comma;
        //   case ":" -> TokenType.Colon;
        //   case ";" -> TokenType.Semicolon;
        //   case "\n" -> TokenType.EOL;
        //   case "\0" -> TokenType.EOT;
        //   case " ", "\t" -> TokenType.Skip;
        //   default -> TokenType.None;
        // };

        // if (type != TokenType.None) it.next(); 
      }
    }
    
    return new Token(type, src);
  }

  public int precedence() {
    return switch (type) {
      case Sub, Add -> 1;
      case Mult, Div -> 2;
      case Pow, Mod -> 3;
      case Factorial -> 5;
      default -> 0;
    };
  }

  public String toString() {
    return src;
  }
}