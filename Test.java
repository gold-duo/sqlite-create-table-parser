import java.util.List;

public class Test {
	public static void main(String []args) throws Exception{
		final String  Test=" create temporary table\n/*MLComment*/ IF NOT EXISTS [table_name] (\n[a1] int  unique not null,b1 double(22) primary key,c1 string(1,2) AUTOINCREMENT,e1 float not null,ff char default(0.123) )--SLComment";
		Parser parser = new Parser(new Lexer(Test));
		try{
			parser.createTableStatement();
			List<TableNode> list = parser.getTableNodes();
			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i));
				List<ColumnNode> cns = list.get(i).getColumnNodes();
				for (int j = 0; j < cns.size(); j++) {
					System.out.println(cns.get(j));
				}
			}
		}catch ( Exception ex){
			System.out.println(ex.getMessage());
		}
	
	}
}
