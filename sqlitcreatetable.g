grammar sqlitcreatetable;

@members{
private boolean isType(String id){
	id=id.toLowerCase();
	return id.equals("int")|| id.equals("integer") 
			||id.equals("bool")||id.equals("boolean")
			||id.equals("long")
			||id.equals("short")||id.equals("byte")
			||id.equals("float")
			||id.equals("real")||id.equals("double")
			||id.equals("blob")
			||id.equals("text")||id.equals("varchar")||id.equals("nvarchar")||id.equals("string")||id.equals("char");
}
}

createTableStatment
	:	'create' (temp='temp'|temp='temporary')? 'table' ('if' 'not' 'exists')?
	 name
	 columnList ';'?
	 {
	 	System.out.print(($temp.text!=null? "temporary ":"") + "table:"+$name.text);
	 }
	;

columnList
	:	'(' column (',' column)* ')' 
	;
	
column	:	
	name
	type typelimit?
	constainst*
	{
	 	System.out.println("column:" +$name.text +" "+$type.text);
	}
	;

typelimit
	:	'(' a=INT ( ',' b=INT)? ')'
	{
		if($a.text!=null && $b.text!=null){
			System.out.print("(" +$a.text+ ","+$b.text+")");
		}else if($a.text!=null ){
			System.out.print("(" +$a.text+")");
		}
	}
	;
	
type	:	{ isType( input.LT(1).getText() ) }?ID
	;

constainst
	:	'primary' 'key'	{System.out.print(" primary key"); }
	|	'unique'	{System.out.print(" unique"); }
	|	'default' '(' (v=INT|v=FLOAT|v=STRING) ')'	{System.out.print(" default("+$v.text+")"); }
	|	'not' 'null'	{System.out.print(" not null"); }
	|	'autoincrement' {System.out.print(" autoincrement"); }
	;

name	:	'[' ID ']'
	|	ID
	;


ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

INT :	'0'..'9'+
    ;

FLOAT
    :   ('0'..'9')+ '.' ('0'..'9')* EXPONENT?
    |   '.' ('0'..'9')+ EXPONENT?
    |   ('0'..'9')+ EXPONENT
    ;

COMMENT
    :   '--' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    |   '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    ;

STRING
    :  '\'' ( ESC_SEQ | ~('\\'|'\'') )* '\''
    ;

fragment
EXPONENT : ('e'|'E') ('+'|'-')? ('0'..'9')+ ;

fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
    
 WS  :   ( ' '
        | '\t'
        | '\r'
        | '\n'
        ) {$channel=HIDDEN;}
    ;