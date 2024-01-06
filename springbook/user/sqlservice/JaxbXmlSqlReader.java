package springbook.user.sqlservice;

public class JaxbXmlSqlReader implements SqlReader{

	private static final String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
	
	// SqlReader의 특정 구현 방법에 종속되는 프로퍼티
	private String sqlmapFile = DEFAULT_SQLMAP_FILE;
	
	public void setSqlmapFile(String sqlMapFile) {this.sqlmapFile = sqlMapFile;}
	
	@Override
	public void read(SqlRegistry sqlRegistry) {
		// JAXB API를 이용해 SQL을 읽어오는 코드
	}
}
