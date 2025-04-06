import java_cup.runtime.*;
%%
/* -----------------Options and Declarations Section----------------- */
%class Scanner
%line
%column
%cup

%{
StringBuffer stringBuffer = new StringBuffer();
private Symbol symbol(int type) {
   return new Symbol(type, yyline, yycolumn);
}
private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
}
%}

LineTerminator = \r|\n|\r\n
WhiteSpace     = {LineTerminator} | [ \t\f]
Identifier = [a-zA-Z_$][a-zA-Z0-9_$]*
FuncParBrac = ")"{WhiteSpace}* "{" // used to detect func def rather than func call

%state STRING

%%
/* ------------------------Lexical Rules Section---------------------- */

<YYINITIAL> {
/* operators */
 "+"            { return symbol(sym.CONCAT); }
 "("            { return symbol(sym.LPAREN); }
 ")"            { return symbol(sym.RPAREN); }
 "}"            { return symbol(sym.RBRAC); }
 ","            { return symbol(sym.COMMA); }
 "prefix"       { return symbol(sym.PREFIX); }
 "suffix"       { return symbol(sym.SUFFIX); }
 "="            { return symbol(sym.EQUAL); }
 "reverse"      { return symbol(sym.REVERSE); }
 "if"           { return symbol(sym.IF); }
 "else"         { return symbol(sym.ELSE); }
 \"             { stringBuffer.setLength(0); yybegin(STRING); }
 {WhiteSpace}   { /* just skip what was found, do nothing */ }
 {Identifier}   { return symbol(sym.IDENTIFIER, new String(yytext())); }
 {FuncParBrac}  { return symbol(sym.FUNCPARBRAC, new String(yytext())); }
}

<STRING> {
      \"                             { yybegin(YYINITIAL);
                                       return symbol(sym.STRING_LITERAL, stringBuffer.toString()); }
      [^\n\r\"\\]+                   { stringBuffer.append( yytext() ); }
      \\t                            { stringBuffer.append('\t'); }
      \\n                            { stringBuffer.append('\n'); }

      \\r                            { stringBuffer.append('\r'); }
      \\\"                           { stringBuffer.append('\"'); }
      \\                             { stringBuffer.append('\\'); }
}

/* No token was found for the input so through an error.  Print out an
   Illegal character message with the illegal character that was found. */
[^]                    { throw new Error("Illegal character <"+yytext()+">"); }
