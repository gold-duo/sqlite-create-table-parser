import java.util.HashMap;
import java.util.Map;


public class Token {
	
    public final static String[] TOKENS = { "CREATE", "TABLE", "UNIQUE",
	    "NULL", "PRIMARY", "KEY", "NOT", "AUTOINCREMENT", "DEFAULT",
	    "CHECK", "IF", "EXISTS", "TEMP", "TEMPORARY", "EOF", "SEMI", "ID",
	    "INT", "FLOAT", "STING", "SL_COMMENT", "ML_COMMENT", "LPAREN", "RPAREN", "LBRACKET", "RBRACKET", "COMMA" 
	    ,"QUOTE","DOUBLE_QUOTE"};

    // token type
    public static final int CREATE = 0, TABLE = 1, UNIQUE = 2, NULL = 3,
	    PRIMARY = 4, KEY = 5, NOT = 6, AUTOINCREMENT = 7, DEFAULT = 8,
	    CHECK = 9, IF = 10, EXISTS = 11, TEMP = 12, TEMPORARY = 13,
	    EOF = 14, SEMI = EOF + 1, ID = EOF + 2, INT = EOF + 3,
	    FLOAT = EOF + 4, STRING = EOF + 5, SL_COMMENT = EOF + 6,
	    ML_COMMENT = EOF + 7, LPAREN = EOF + 8, RPAREN = EOF + 9,
	    LBRACKET = EOF + 10, RBRACKET = EOF + 11, COMMA = EOF + 12,
	    QUOTE=EOF+13,DOUBLE_QUOTE=EOF+14;

	private static final Map<String,Integer> KEYWORDS=new HashMap<String,Integer>(TEMPORARY-CREATE+1);
	
	public final int type;
	public final String text;
	public Token(int type, String text) {
		this.type = type;
		this.text = text;
	}
	
	static{
	    for(int i=CREATE;i<=TEMPORARY;i++){
	    	KEYWORDS.put(TOKENS[i], i);
	    }
	}
    public String toString() {
        return  TOKENS[type] +"=<"+text +">";
    }
    
	public static Token lookup(String str){
	    Integer type=KEYWORDS.get(str.toUpperCase());
	    return new Token(type==null? ID:type, str);
	}
}
