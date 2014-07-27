import java.util.ArrayList;
import java.util.List;


public class TableNode extends Node{

	public boolean mTemporary;
	private List<ColumnNode> mColumnNodes= new ArrayList<ColumnNode>();
	public TableNode(Token tk,boolean isTemp) {
		super(tk);
		mTemporary=isTemp;
	}

	public String getName(){
		return token.text;
	}
	
	public String toString() {
		return "tbl--"+(mTemporary? "temporaray ":"")+getName();
	}
	
	public boolean isTemporary(){
		return mTemporary;
	}
	
	public void addColumnNode(ColumnNode cn){
		mColumnNodes.add(cn);
	}
	
	public List<ColumnNode> getColumnNodes(){
		return mColumnNodes;
	}
}
