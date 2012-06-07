/** Based on the arc lexer (http://code.google.com/p/intelli-arc/) **/

package jsyntaxpane.lexers;

import jsyntaxpane.Token;
import jsyntaxpane.TokenType;

%%


%class RLexer
%extends DefaultJFlexLexer
%unicode
%public
%char

%type Token

%{

    public RLexer() {
        super();
    }

    private static final byte PAREN            = 1;
    private static final byte BRACKET          = 2;
    private static final byte CURLY            = 4;

    @Override
    public int yychar() {
        return yychar;
    }

%}

/*
Macro Declarations

These declarations are regular expressions that will be used latter in the
Lexical Rules Section.
*/

/* A line terminator is a \r (carriage return), \n (line feed), or \r\n. */
EOL = (\r|\n|\r\n)*
//WHITE_SPACE= {EOL} | [ \t\f]
WHITE_SPACE= [ \t\f]
SECTION_COMMENT = [#]{5,120}[\r\n]{1} [#]{2,5} [^\r\n]*
COMMENT = "#"[^\r\n]*

/* A identifier integer is a word beginning a letter between A and Z, a and z,
or an underscore followed by zero or more letters between A and Z, a and z,
zero and nine, or an underscore. */
SYMBOL = [A-Za-z.][A-Za-z_0-9._]*


/* A literal integer is is a number beginning with a number between one and nine
followed by zero or more numbers between zero and nine or just a zero. */
IntLiteral = 0 | [1-9][0-9]*[L]?

Exponent = [eE] [+-]? [0-9]+
FLit1    = [0-9]+ \. [0-9]*
FLit2    = \. [0-9]+
FLit3    = [0-9]+
DoubleLiteral = ({FLit1}|{FLit2}|{FLit3}) {Exponent}?


//StringCharacter = [^\r\n]
// picked up from arc.flex :
EscapeSequence=\\[^\r\n]
//todo allow for linebreaks in strings and for single quot quoting
STRING_DQUOTE=\"([^\\\"]|{EscapeSequence})*(\"|\\)?
STRING_SQUOTE='([^\\\']|{EscapeSequence})*('|\\)?

//%state STRING

/* ------------------------Lexical Rules Section---------------------- */

/*
This section contains regular expressions and actions, i.e. Java code, that
will be executed when the scanner matches the associated regular expression. */

/* YYINITIAL is the state at which the lexer begins scanning.  So these regular
expressions will only be matched if the scanner is in the start state
YYINITIAL. */

%%

<YYINITIAL> {
  {WHITE_SPACE} { }
  {EOL} { }
  {SECTION_COMMENT} { return token(TokenType.COMMENT); }
  {COMMENT} { return token(TokenType.COMMENT); }

  // r keywords
  "function" |
  "for"      |
  "while"    |
  "if"       |
  "else"     |
  "break"    |
  "next"     |
  "repeat"   |
  "in"       |
  "NULL"     |
  "T"        |
  "TRUE"     |
  "F"        |
  "FALSE"    |
  "NA"       |
  "NaN"      |
  "Inf"      |
  "..." { return token(TokenType.KEYWORD); }

  "NULL" { return token(TokenType.TYPE); }

  // data types
  "matrix"       |
  "data.frame"   |
  "c"            |
  "list"         |
  "vector"       |
  "array"        |
  "class"        |
  "character"    |
  "numeric"      |
  "date"         |

  // conversion functions
  "as.character"  |
  "as.data.frame" |
  "as.matrix"     |
  "as.numeric"    |
  "as.Date"       |

  // dimension related
  "nrow"         |
  "ncol"         |
  "dim"          |
  "length"       |
  "seq_len"      |

  // names
  "names"        |
  "dimnames"     |
  "colnames"     |
  "rownames"     |

  // arithmetic functions
  "mean"         |
  "sum"          |
  "colSums"      |
  "rowSums"      |
  "abs"          |
  "min"          |
  "max"          |
  "median"       |
  "power"        |
  "rank"         |
  "real"         |
  "sin"          |
  "cos"          |
  "sign"         |
  "sqrt"         |

  // apply and friends
  "apply"        |
  "sapply"       |
  "tapply"       |
  "mapply"       |

  // string functions
  "str"          |
  "tolower"      |
  "toupper"      |
  "strsplit"     |
  "strtrim"      |
  "cat"          |
  "paste"        |
  "cut"          |
  "substring"    |

  // transformation
  "with"         |
  "transform"    |
  "subset"       |
  "merge"        |
  "order"        |
  "sort"         |
  "rep"          |

  // system
  "options"      |
  "library"      |
  "browser"      |
  "do.call"      |
  "get"          |
  "environment"  |
  "missing"      |
  "mode"         |
  "q"            |
  "quit"         |
  "rm"           |
  "remove"       |
  "setwd"        |
  "getwd"        |
  "stop"         |
  "stopifnot"    |

  // other
  "setdiff"      |
  "setequal"     |
  "match"        |
  "unique"       |
  "rbind"        |
  "cbind"        |
  "col"          |
  "row"          |
  "unlist"       |
  "unclass"      |
  "attr"         |
  "attributes"   |
  "drop"         |
  "na.rm"        |
  "is.na"  { return token(TokenType.KEYWORD2); }

  {STRING_SQUOTE} | {STRING_DQUOTE} { return token(TokenType.STRING); }
  {SYMBOL} { token(TokenType.TYPE2); }

  {IntLiteral} | {DoubleLiteral}  { return token(TokenType.NUMBER); }

  "(" { return token(TokenType.OPERATOR, PAREN); }
  ")" { return token(TokenType.OPERATOR, -PAREN); }
  "{" { return token(TokenType.OPERATOR, CURLY); }
  "}" { return token(TokenType.OPERATOR, -CURLY); }
  "[" { return token(TokenType.OPERATOR, BRACKET); }
  "]" { return token(TokenType.OPERATOR, -BRACKET); }

  // operators
  ";"   |
  ":"   |
  ","   |
  "!"   |
  "=="  |
  ">"   |
  "<"   |
  ">="  |
  "<="  |
  "!="  |
  "&"   |
  "&&"  |
  "|"   |
  "||"  |
  "="   |
  "<-"  |
  "->"  |
  "-"   |
  "+"   |
  "~"   |
  "*"   |
  "%"   |
  "/"   |
  "^"   |
  "%%"  |
  "%/%" | "%*%" | "%o%" | "%x%" | %in% { return token(TokenType.OPERATOR); }


   "$"   |
   "@"   |
   "?"   |
   "::"  |
   ":::" { return token(TokenType.TYPE2); }
}

. { return token(TokenType.ERROR); }
//<<EOF>>  { return null; }