grammar OscExpr;

/** The start rule; begin parsing here. */
prog:   '${' expr '}'; 

expr:   func=POW '(' expr ',' expr ')' # FunctionTwoArguments
        | func=SQRT '(' expr ')' # FunctionOneArgument
        | '(' type=TYPECAST ')' expr # Typecast
	| MINUS expr	 # UnaryMinus
	| expr op=(MULTIPLY | DIVIDE | MODULO) expr # MultiDivMod
	| expr op=(PLUS|MINUS) expr # PlusMinus
	| num=NUMBER_LITERAL #NumLiteral
	| ID # IdExpr
	| '(' expr ')'  # SquaredExpr
	;

SQRT: 'sqrt';
POW: 'pow';
TYPECAST :  ('int'|'double'| 'unsignedInt' | 'unsignedShort');
ID  :   '$'[a-zA-Z_][a-zA-Z0-9_]* ;
NUMBER_LITERAL
    :   INT '.' [0-9]+ EXP? // 1.35, 1.35E-9, 0.3, -4.5
    |   INT EXP             // 1e10 -3e4
    |   INT                 // -3, 45
    ;
NEWLINE:'\r'? '\n' -> skip ;   // toss out newline
WS  :   [ \t]+ -> skip ; // toss out whitespace


MULTIPLY : '*';
DIVIDE : '/';
MODULO : '%';
PLUS : '+';
MINUS : '-';
	
fragment INT :   '0' | [1-9] [0-9]* ; // no leading zeros
fragment EXP :   [Ee] [+\-]? INT ; // \- since - means "range" inside [...]
