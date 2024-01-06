package springbook.user.sqlservice;

public class JaxbXmlSqlReader implements SqlReader{

	private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
	
	// SqlReader�� Ư�� ���� ����� ���ӵǴ� ������Ƽ
	private String sqlmapFile = DEFAULT_SQLMAP_FILE;
	
	public void setSqlmapFile(String sqlMapFile) {this.sqlmapFile = sqlMapFile;}
	
	@Override
	public void read(SqlRegistry sqlRegistry) {
		// JAXB API�� �̿��� SQL�� �о���� �ڵ�
	}
}
