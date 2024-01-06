package springbook.user.sqlservice;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

public class XmlSqlService implements SqlService, SqlRegistry, SqlReader{
	
	private String sqlmapFile;
	
	//sqlMap은 SqlRegistry 구현의 일부가 된다. 따라서 외부에서 직접 접근할 수 없다.
	private Map<String, String> sqlMap = new HashMap<String, String>();
	
	private SqlReader sqlReader;
	private SqlRegistry sqlRegistry;
	
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}
	
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}
	
	@PostConstruct
	public void loadSql() {
		this.sqlReader.read(this.sqlRegistry);
	}
	
	@Override
	public void read(springbook.user.sqlservice.SqlRegistry sqlRegistry) {
		// 읽어오기.
	}
	
	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if (sql == null) throw new SqlNotFoundException("key에 대한 SQL을 찾을 수 없습니다.");
		else return sql;
	}
	
	@Override
	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		try {
			return this.sqlRegistry.findSql(key);
		} catch(SqlNotFoundException e) {
			throw new SqlRetrievalFailureException(e);
		}
	}
}
