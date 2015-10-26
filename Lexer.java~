public class Lexer  {
	public final static String[] TOKENS = {"END", ";", "ID", "INT", "FLOAT",
			"STRING", "--", "/**/", "CREATE", "TABLE", "(",
			")", "[", "]", ",", "UNIQUE", "NULL",
			"PRIMARY", "KEY", "NOT", "AUTOINCREMENT", "DEFAULT", "CHECK", "IF",
			"EXISTS", "DEFAULT", "TEMP", "TEMPORARY"};

	// token type
	public static final int EOF = 0, SEMI = 1, ID = 2, INT = 3, FLOAT = 4,
			STRING = 5, SL_COMMENT = 6, ML_COMMENT = 7, CREATE = 8, TABLE = 9,
			LPARENT = 10, RPARENT = 11, LBRACKET = 12, RBRACKET = 13,
			COMMA = 14, UNIQUE = 15, NULL = 16, PRIMARY = 17, KEY = 18,
			NOT = 19, AUTOINCREMENT = 20, DEFAULT = 21, CHECK = 22, IF = 23,
			EXISTS = 24, TEMP = 25, TEMPORARY = 26;

	//
	private final String mInput;
	private int pIn;
	private int mP;
	private char[] mBuffer;
	private final int K;
	private static final char END = (char) -1;
	
	public Lexer(String input, int k) {
		mInput = input;
		K = k;
		mBuffer = new char[k];
		consume(k);
	}
	//
	
	public Lexer(String input) {
		this(input,14);
	}

	public Token nextToken() throws Exception  {
		char ch,lc2;
		while ((ch = LC(1)) != END) {
			switch (ch) {
				case ' ' : case '\t' : case '\n' : case '\r' :
					doWS();
					continue;
				case ',' :
					consume();
					return new Token(COMMA, ",");
				case '(' :
					consume();
					return new Token(LPARENT, "(");
				case ')' :
					consume();
					return new Token(RPARENT, ")");
				case '[' :
					consume();
					return new Token(LBRACKET, "[");
				case ']' :
					consume();
					return new Token(RBRACKET, "]");
				case ';' :
					consume();
					return new Token(SEMI, ";");
				case '\'' :
					return new Token(STRING, getSTR());
				case '_' :
					return new Token(ID, getID());
				case '-':
					 lc2 = LC(2);
					 if(lc2=='-'){
						 doSLC();
					 }else if(isDigit(lc2)){
						 return speculateNumber();
					 }else{
						throw new Exception("-后<"+lc2 +">无法识别!");
					}
					break;
				case '/':
					lc2 = LC(2);
					if(lc2=='*'){
						doMLC();
					}else{
						throw new Exception("/后<"+lc2 +">无法识别!");
					}
					break;
				case '.':
						if( isDigit(LC(2))){
							return speculateNumber();
						}else{
							throw new Exception(LC(2)+":不是数字!" );
						}
				case '0':case '1':case '2':case '3':case '4':case '5':case '6':case '7':case '8':case '9':
				    return speculateNumber();
				default :
				    return speculateKW(LCLower(1));
			}
		}

		return new Token(EOF, "EOF");
	}

	private Token speculateKW(char ch) throws Exception{
		char lc2;
		switch (ch) {
			case 'a' :
				if( isKW("autoincrement") ) return new Token(AUTOINCREMENT, "autoincrement");
				break;
			case 'b':break;
			case 'c' :
				lc2 = LCLower(2);
				if (lc2 == 'r') {
					if( isKW("create") ) return new Token(CREATE, "create");
				} else if (lc2 == 'h') {
					if( isKW("check") ) return new Token(CHECK, "check");
				}
				break;
			case 'd' :
				if( isKW("default") ) return new Token(DEFAULT, "default");
				break;
			case 'e':
				if( isKW("exists") ) return new Token(EXISTS, "exists");
				break;
			case 'f':
			case 'g':
			case 'h':
				break;
			case 'i':
				if( isKW("if") ) return new Token(IF, "if");
				break;
			case 'j': break;
			case 'k' :
				if( isKW("key") ) return new Token(KEY, "key");
				break;
			case 'l': break;
			case 'n' :
				lc2= LCLower(2);
				if (lc2 == 'u') {
					if( isKW("null") ) return new Token(NULL, "null");
				} else if (lc2 == 'o') {
					if( isKW("not") ) return new Token(NOT, "not");
				}
				break;
			case 'm': 
			case 'o': 
				break;
			case 'p' :
				if( isKW("primary") ) return new Token(PRIMARY, "primary");
				break;
			case 'q': 
			case 'r': 
			case 's': 
				break;
			case 't' :
				lc2 = LCLower(2);
				if(lc2=='a'){
					if( isKW("table") ) return new Token(TABLE, "table");
				}else if (lc2=='e'){
					if(isKW("temp")){
						return new Token(TEMP, "temp");
					}else if(isKW("temporary")){
						return new Token(TEMPORARY, "temporary");
					}
				}
				break;
			case 'u' :
				if( isKW("unique") ) return new Token(UNIQUE, "unique");
				break;
			case 'v': 
			case 'w': 
			case 'x': 
			case 'y': 
			case 'z': 
				break;
			default :
					throw new Exception(ch+":无法识别!");
		}
		return new Token(ID, getID());
	}
	
	private Token speculateNumber() throws Exception{
		StringBuffer sb = new StringBuffer();
		boolean iF=false,iNegative =false;
		for(;;){
			char ch=LC(1) ;
			if( ch=='.' ){
				if(isDigit(LC(2))){
					iF=true;
				}else{
					throw new Exception("多余的点符号!");
				}
			}else if( ch=='-' && !iNegative){
				iNegative=true;
			}else if( !isDigit(ch)){
				break;
			}
			sb.append(ch);
			consume();
		}
		return new Token( iF? FLOAT:INT,sb.toString());
	}
		
	private boolean isKW(String str) {
		int n = str.length();
		for (int i = 1; i < n; i++) {
			char ch = LCLower(i + 1);
			if (ch != str.charAt(i)) {
				return false;
			}
		}
		char la= LC(n+1);
		if(isLetter(la) ||isDigit(la)||isUL(la)){
			return false;
		}
		consume(n);
		return true;
	}

	private String getID() {
		StringBuffer sb = new StringBuffer();
		char ch ;
		do {
			sb.append(LC(1));
			consume();
			ch=LC(1) ;
		} while (isLetter(ch) || isUL(ch) || isDigit(ch));
		return sb.toString();
	}

	private String getSTR() throws Exception {
		consume();
		StringBuffer sb = new StringBuffer();
		char ch ;
		for(;;) {
			ch=LC(1) ;
			if(ch=='\''){
				break;
			}else if (ch=='\r'||ch=='\n'|| ch==END){
				throw new Exception("未识别到字符串结束符号!");
			}else if (ch=='\\' &&  LC(2) =='\''){
				sb.append("\\'");
				consume(2);
			}else{
				sb.append(ch);
				consume();
			}
		}
		consume();
		return sb.toString();
	}
	
	private void doSLC(){
		consume(2);
		char ch;
		while( (ch=LC(1))!='\r' &&  ch!='\n' && ch!=END){
			consume();
		}
		LOG("SLComment");
	}
	
	private void doMLC() throws Exception{
		consume(2);
		for (;;) {
			char lc1 = LC(1),lc2 = LC(2);
			if( lc1=='*' && lc2=='/'){
				break;
			}else if (lc1 == END || lc2 == END) {
				throw new Exception("未识别到注释结束符!");
			}
			consume();
		}
		consume(2);
		LOG("MLComment");
	}
	
	private void doWS() {
		while (isWS()) {
			consume();
		}
	}

	private boolean isUL(char ch){
		return ch=='_';
	}
	private boolean isDigit(char ch) {
		return (ch >= '0' && ch <= '9');
	}

	private boolean isLetter(char ch) {
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
	}

	private boolean isWS() {
		char ch = LC(1);
		return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
	}
	
	//
	private char NC() {
		return pIn >= mInput.length()? END:mInput.charAt(pIn++);
	}

	private char LCLower(int i) {
		char ch = LC(i);
		if ((ch >= 'A') && (ch <= 'Z')) {
			ch += 32;
		}
		return ch;
	}

	private char LC(int i) {
		return mBuffer[(mP + i - 1) % K];
	}

	private void consume(int n) {
		for (int i = 0; i < n; i++) {
			mBuffer[mP] = NC();
			mP = (mP + 1) % K;
		}
	}

	public int getStreamPos(){
		return pIn+(mP%K);
	}
	public String getString(){
		return mInput;
	}
	
	private void consume() {
		consume(1);
	}
	//
	private void LOG(String text){
		System.out.println(text);
	}
}// end class
