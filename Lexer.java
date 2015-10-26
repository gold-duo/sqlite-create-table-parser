public class Lexer {
	/** 接受 */
	private static final int ACT_ACCEPTED = 1;
	/** 不收集 */
	private static final int ACT_NOT_COLLECT = 2;
	/** 不用向前推进 */
	private static final int ACT_NOT_ADVANCE = 4;

	/** 对应状态的动作 */
	private static final byte[] STATE_ACT = new byte[29];
	
	private static final byte[][] STATES = {
//		0	1	2	3	4	5	6	7	8	9	10	11	12	13	14	15	16	18
//		digit	letter	.	-	_	/	*	single_quote	double_quote	new_line	ws	,	(	)	[	]	;	EOF
	/*0*/{3,	7,	-1,	1,	7,	8,	-1,	11,	13,	0,	0,	22,	23,	24,	25,	26,	27,	28},
	/*1*/{3,	-1,	-1,	2,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1},
	/*2*/{4,	4,	4,	4,	4,	4,	4,	4,	4,	15,	4,	4,	4,	4,	4,	4,	4,	15},
	/*3*/{3,	17,	5,	17,	17,	17,	17,	17,	17,	17,	17,	17,	17,	17,	17,	17,	17,	17},
	/*4*/{4,	4,	4,	4,	4,	4,	4,	4,	4,	15,	4,	4,	4,	4,	4,	4,	4,	15},
	/*5*/{6,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1},
	/*6*/{6,	16,	16,	16,	16,	16,	16,	16,	16,	16,	16,	16,	16,	16,	16,	16,	16,	16},
	/*7*/{7,	7,	18,	18,	7,	18,	18,	18,	18,	18,	18,	18,	18,	18,	18,	18,	18,	18},
	/*8*/{-1,	-1,	-1,	-1,	-1,	-1,	9,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1},
	/*9*/{9,	9,	9,	9,	9,	9,	10,	9,	9,	9,	9,	9,	9,	9,	9,	9,	9,	-1},
	/*10*/{9,	9,	9,	9,	9,	19,	10,	9,	9,	9,	9,	9,	9,	9,	9,	9,	9,	-1},
	/*11*/{12,	12,	12,	12,	12,	12,	12,	20,	12,	-1,	12,	12,	12,	12,	12,	12,	12,	-1},
	/*12*/{12,	12,	12,	12,	12,	12,	12,	20,	12,	-1,	12,	12,	12,	12,	12,	12,	12,	-1},
	/*13*/{14,	14,	14,	14,	14,	14,	14,	14,	21,	-1,	14,	14,	14,	14,	14,	14,	14,	-1},
	/*14*/{14,	14,	14,	14,	14,	14,	14,	14,	21,	-1,	14,	14,	14,	14,	14,	14,	14,	-1},
			};
	private static final int 
			T_DIGIT = 0, T_LETTER = 1, T_DOT = 2,
			T_BAR = 3/*-*/, T_UNDERLINE = 4, T_SPRIT = 5/* / */, T_START = 6,
			T_QUOTE = 7, T_DOUBLE_QUOTE = 8, T_NEWLINE = 9, T_WS = 10,
			T_COMMA=11/*,*/,T_LPAREN=12/*(*/,T_RPAREN=13/*)*/,
			T_LBRACKET=14/*[*/,T_RBRACKET=15/*[*/,T_SEMI=16/*;*/,
			T_EOF=17,T_UNKNOW=18;
	
		private static final int F_S_COMMENT = 15, F_S_FLOAT = 16, F_S_INT = 17,
			F_S_ID = 18, F_S_M_COMMENT = 19, F_S_QUOTE = 20,
			F_S_DOUBLE_QUOTE = 21, F_S_COMMA = 22, F_S_LPAREN = 23,
			F_S_RPAREN = 24, F_S_LBRACKET = 25, F_S_RBRACKET = 26,
			F_S_SEMI = 27, F_S_EOF = 28;

	/* 字符串结尾 */
	static final char END_CHAR = (char) -1;

	/* 错误状态 */
	private static final int ERROR_STATE = -1;

	private final String mInput;
	private int mPointer;
	
	static {
		// 接受状态
		for (int i = 15; i <= 28; i++) {
			STATE_ACT[i] = ACT_ACCEPTED;
		}
		// 空白字符、换行符
		STATE_ACT[0] |= ACT_NOT_COLLECT;

		// 单引号
		STATE_ACT[11] |= ACT_NOT_COLLECT;
		
		//多行注释
//		STATE_ACT[8] |= ACT_NOT_COLLECT;
//		STATE_ACT[9] |= ACT_NOT_COLLECT;
//		STATE_ACT[10] |= ACT_NOT_COLLECT;
//		STATE_ACT[19] |= ACT_NOT_COLLECT;

		// 双引号
		STATE_ACT[13] |= ACT_NOT_COLLECT;
		STATE_ACT[20] |= ACT_NOT_COLLECT;

		// 单行注释，通过换行符来终结
//		STATE_ACT[2] |= ACT_NOT_COLLECT;
//		STATE_ACT[4] |= ACT_NOT_COLLECT;
		STATE_ACT[15] |= ACT_NOT_COLLECT | ACT_NOT_ADVANCE;

		// number，通过非数字来终结
		STATE_ACT[16] |= ACT_NOT_COLLECT | ACT_NOT_ADVANCE;
		STATE_ACT[17] |= ACT_NOT_COLLECT | ACT_NOT_ADVANCE;

		// ID，通过非（字符、数字、下划线）终结
		STATE_ACT[18] |= ACT_NOT_COLLECT | ACT_NOT_ADVANCE;

		// 终结符EOF
		STATE_ACT[28] |= ACT_NOT_COLLECT | ACT_NOT_ADVANCE;
	}

	public Lexer(String input){
		mInput=input;
		mPointer=0;
	}
	
	private void advace() {
		// 用到EOF终结符，所以可以达到最后长度
		if ((1 + mPointer) <= mInput.length()) {
			mPointer++;
		}
	}

	private final char lookChar() {
		return mPointer < mInput.length() ? mInput.charAt(mPointer) : END_CHAR;
	}

	private int transition(int charType, int currentState) {
		if (currentState < STATES.length && charType < STATES[0].length) {
			return STATES[currentState][charType];
		}
		return ERROR_STATE;
	}

	public Token next() throws Exception {
		StringBuffer sb =null;;
		int state=0;
		for (;;) {
			final char ch = lookChar();
			final int charType = tranColumn(ch);
			if (charType == T_UNKNOW) {
				throw new Exception("unknown char(" + ch + ")--"+ reportErrorPos());
			}
			final int newState = transition(charType, state);
			if (newState == ERROR_STATE) {
				throw new Exception("ERROR_STATE char(" + ch + ")--"+ reportErrorPos());
			}

			final int state_act = STATE_ACT[newState];
			// 收集
			if ((state_act & ACT_NOT_COLLECT) != ACT_NOT_COLLECT) {
				if(sb==null){
					sb= new StringBuffer();
				}
				sb.append(ch);
			}
			// 停留
			if ((state_act & ACT_NOT_ADVANCE) != ACT_NOT_ADVANCE) {
				advace();
			}

			if ((state_act & ACT_ACCEPTED) == ACT_ACCEPTED) {
				return createToken(newState, sb == null ? "" : sb.toString());
			} else {
				state = newState;
			}
		}
	}
	
	private final int tranColumn(char ch){
		switch(ch){
		case '0':case '1':case '2':case '3':case '4':case '5':case '6':case '7':case '8':case '9':return T_DIGIT;
		case '.': return T_DOT;
		case '-':return T_BAR;
		case '_':return T_UNDERLINE;
		case '/':return T_SPRIT;
		case '*':return T_START;
		case '\'':return T_QUOTE;
		case '\"':return T_DOUBLE_QUOTE;
		case '\n' :  return T_NEWLINE;
		case ' ' : case '\t' :case '\r' :return T_WS;
		case ',' : return T_COMMA;
		case '(' :return T_LPAREN;
		case ')' :return T_RPAREN;
		case '[' :return T_LBRACKET;
		case ']' :return T_RBRACKET;
		case ';' :return T_SEMI;
		case END_CHAR: return T_EOF;
		default:
			if((ch>='a'&&ch<='z')||(ch>='A'&&ch<='Z')){
				return T_LETTER;
			}
		}
		return T_UNKNOW;
	}
	
	private Token createToken(int acceptState,String val) throws Exception{
		switch(acceptState){
		case F_S_COMMENT: return new Token(Token.SL_COMMENT,val);
		case F_S_FLOAT: return new Token(Token.FLOAT,val);
		case F_S_INT: return new Token(Token.INT,val);
		case F_S_ID:return Token.lookup(val);
		case F_S_M_COMMENT:return new Token(Token.ML_COMMENT,val);
		case F_S_QUOTE:return new Token(Token.QUOTE,val);
		case F_S_DOUBLE_QUOTE:return new Token(Token.DOUBLE_QUOTE,val);
		case F_S_COMMA:return new Token(Token.COMMA,val);
		case F_S_LPAREN:return new Token(Token.LPAREN,val);
		case F_S_RPAREN:return new Token(Token.RPAREN,val);
		case F_S_LBRACKET:return new Token(Token.LBRACKET,val);
		case F_S_RBRACKET:return new Token(Token.RBRACKET,val);
		case F_S_SEMI:return new Token(Token.SEMI,val);
		case F_S_EOF:return new Token(Token.EOF, "EOF");
		}
		throw new Exception("unknown acceptState("+acceptState+"):"+val+"--"+reportErrorPos());
	}
	
	private String reportErrorPos(){
		return mPointer<mInput.length()? mInput.substring(0, mPointer):mInput;
	}
}//end class Lexer
