package springbook.user.sqlservice;

import javax.annotation.PostConstruct;

public class BaseSqlService implements SqlService{
	
	private SqlReader sqlReader;
	private SqlRegistry sqlRegistry;
	
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}
	
	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}

	
	public void loadSql() {
		this.sqlReader.read(this.sqlRegistry);
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
