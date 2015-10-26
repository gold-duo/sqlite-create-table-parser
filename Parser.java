import java.util.List;
import java.util.ArrayList;

public class Parser {
	private final Lexer mLexer;
	private Token []mTokens;
	private int P;
	private final int K;
	
	private List<TableNode> mTableNodes= new ArrayList<TableNode>();
	
	private TableNode mTableNode=null;
	private ColumnNode mColumnNode=null;
	public Parser(Lexer lexer) throws Exception{
		mLexer=lexer;
		K=1;
		mTokens = new Token[K];
		consume(K);
	}
	
	public void createTableStatement() throws Exception{
		boolean isTemporary=false;
		//create (temp|temporary)? table
		match(Token.CREATE);
		if(!tryMatch(Token.TEMP)){
			isTemporary=tryMatch(Token.TEMPORARY);
		}else{
			isTemporary=true;
		}
		match(Token.TABLE);
		
		// if not exists
		if(tryMatch(Token.IF)){
			match(Token.NOT);
			match(Token.EXISTS);
		}
		
		//table name
		tblName(isTemporary);
		
		columnList();
		
		tryMatch(Token.SEMI);
		match(Token.EOF);
	}
	
	private void columnList() throws Exception{
		match(Token.LPAREN);
		column();
		while(tryMatch(Token.COMMA)){
			column();
		}
		match(Token.RPAREN);
	}
	
	private void column() throws Exception{
		colName();
		colType();// type
		
		contraintPrimaryKey();// test primary key
		contraintUnique();// test unique
		contraintAutoincrement();// test Autoincrement
		contraintDefault(); // test default
		contraintNotNull(); // test not null
	}
	
	
	private void tblName(boolean isTemporary) throws Exception {
		Token token=null;
		if (tryMatch(Token.LBRACKET)) {
			if(LA(1)== Token.ID){
				token=LT(1);
//				token.text="["+token.text+"]";
				consume();
				match(Token.RBRACKET);
			}
		} else if (LA(1) == Token.ID) {
			token=LT(1);
			consume();
		}
		
		if(token==null){
			throw new Exception(LT(1).text+":无法识别成表名!");
		}else{
			mTableNode=new TableNode(token,isTemporary);
			mTableNodes.add( mTableNode);
		}
	}
	
	private void colName() throws Exception {
		Token token=null;
		if (tryMatch(Token.LBRACKET)) {
			if(LA(1)== Token.ID){
				token=LT(1);
//				token.text="["+token.text+"]";
				consume();
				match(Token.RBRACKET);
			}
		} else if (LA(1) == Token.ID) {
			token=LT(1);
			consume();
		}
		
		if(token==null){
			throw new Exception(LT(1).text+":无法识别成表名!");
		}else{
			mColumnNode = new ColumnNode(token);
			mTableNode.addColumnNode(mColumnNode);
		}
	}
	
	private void colType() throws Exception {
		Token tk = LT(1);
		if (tk.type == Token.ID && isType(tk.text)) {
//			LOG("colType--" + tk.text);
			mColumnNode.setType(tk.text);
			consume();

			if ( tryMatch(Token.LPAREN) ) {
				if (LA(1) == Token.INT) {
					mColumnNode.setTypeMinSize(Integer.parseInt(LT(1).text));
					consume();

					if ( tryMatch(Token.COMMA)) {
						if (LA(1) == Token.INT) {
							mColumnNode.setTypeMaxSize(Integer.parseInt(LT(1).text));
							consume();
						} else {
							throw new Exception(LT(1).text+ ":不是数字！");
						}
					}
				} else {
					throw new Exception(LT(1).text+ ":不是数字！");
				}
				match(Token.RPAREN);
			}
		} else {
			throw new Exception("无法识别类型:" + tk.text);
		}
	}
	
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
	
	private boolean contraintPrimaryKey() throws Exception{
		if(tryMatch(Token.PRIMARY)){
			if (LA(1) == Token.KEY) {
				mColumnNode.setContraint(ColumnNode.PRIMARYKEY);
				consume();
//				LOG("contraintPrimaryKey");
				return true;
			}else{
				throw new Exception("PRIMARY KEY关键字[KEY]丢失！");
			}
		}
		return false;
	}
	
	private boolean contraintUnique() throws Exception{
		if(LA(1)==Token.UNIQUE){
			mColumnNode.setContraint(ColumnNode.UNIQUE);
			consume();
//			LOG("contraintUnique");
			return true;
		}
		return false;
	}
	
	private boolean contraintAutoincrement() throws Exception{
		if(LA(1)==Token.AUTOINCREMENT){
			mColumnNode.setContraint(ColumnNode.AUTO);
			consume();
//			LOG("contraintAutoincrement");
			return true;
		}
		return false;
	}
	
	private boolean contraintNotNull() throws Exception{
		if(tryMatch(Token.NOT)){
			if( LA(1)==Token.NULL){
				mColumnNode.setContraint(ColumnNode.NOTNULL);
				consume();
//				LOG("contraintNotNull");
				return true;
			}else{
				throw new Exception("NOT NULL关键字[NULL]丢失！");
			}
		}
		return false;
	}
	
	private void defaultVaule() throws Exception{
		Token tk=LT(1);
		int la =tk.type;
		if (la==Token.STRING||la==Token.INT||la==Token.FLOAT){
			mColumnNode.setDefaultValue(la==Token.STRING?  "'"+tk.text+"'": tk.text);
			consume();
		}else{
			throw new Exception(tk.text+":default 值不为数字或字符串!");
		}
	}
	
	private boolean contraintDefault() throws Exception{
		boolean ret =true;
		if(tryMatch(Token.DEFAULT)){
			if(tryMatch(Token.LPAREN)){
				defaultVaule();
				match(Token.RPAREN);
			}else{
				defaultVaule();
			}
			mColumnNode.setContraint(ColumnNode.DEFAULT);
		}else{
			ret=false;
		}
		return ret;
	}
	
//	private void contraintCheck(){
//		
//	}
	
	private Token LT(int i){
		return mTokens[(P+i-1)%K];
	}
	
	private int LA(int i){
		return LT(i).type;
	}
	
	private boolean tryMatch(int type) throws Exception{
		if ( LA(1) == type ) {
			consume();
			return true;
		}
		return false;
	}
	
	private void match(int type) throws Exception {
        if ( LA(1) == type ) {
//        	LOG("match--"+ LT(1));
        	consume();
        } else{
        	throw new Exception(LT(1) .text+" 不匹配 "+Token.TOKENS[type]);
        }
                             
    }
	
	private void consume() throws Exception {
    	consume(1);
    }
    
	private void consume(int n) throws Exception {
		for (int i = 0; i < n;) {
			final Token tk = mLexer.next();
			if (Token.SL_COMMENT == tk.type || Token.ML_COMMENT == tk.type)
				continue;
			mTokens[P] = tk;
			P = (P + 1) % K;
			i++;
		}
	}
	
	private void LOG(String text) {
		// System.out.println(text);
	}

	public List<TableNode> getTableNodes(){
		return mTableNodes;
	}
	//--
}//end class
