package springbook.user.test;

import springbook.issuetracker.sqlservice.UpdatableSqlRegistry;
import springbook.user.sqlservice.ConcurrentHashMapSqlRegistry;

public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{

	@Override
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		return new ConcurrentHashMapSqlRegistry();
	}
	
}
