package springbook.user.sqlservice;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

public class XmlSqlService implements SqlService, SqlRegistry, SqlReader{
	
	private String sqlmapFile;
	
	//sqlMap�� SqlRegistry ������ �Ϻΰ� �ȴ�. ���� �ܺο��� ���� ������ �� ����.
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
		// �о����.
	}
	
	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if (sql == null) throw new SqlNotFoundException("key�� ���� SQL�� ã�� �� �����ϴ�.");
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
