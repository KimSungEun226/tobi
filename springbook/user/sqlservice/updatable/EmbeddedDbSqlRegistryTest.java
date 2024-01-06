package springbook.user.sqlservice.updatable;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import springbook.issuetracker.sqlservice.SqlUpdateFailureException;
import springbook.issuetracker.sqlservice.UpdatableSqlRegistry;
import springbook.user.test.AbstractUpdatableSqlRegistryTest;

public class EmbeddedDbSqlRegistryTest extends AbstractUpdatableSqlRegistryTest{

	EmbeddedDatabase db;
	
	@Override
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		db = new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.HSQL).addScript(
					"classpath:springbook/user/sqlservice/updatable/sqlRegistrySchema.sql")
				.build();
		
		EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
		embeddedDbSqlRegistry.setDataSource(db);
		
		return embeddedDbSqlRegistry;
	}
	
	@After
	public void tearDown() {
		db.shutdown();
	}
	
	@Test
	public void transactionalUpdate() {
		// �ʱ� ���� Ȯ��
		// �̹� ����Ŭ������ �ٸ� �׽�Ʈ �޼ҵ忡�� Ȯ���ϱ� ������ Ʈ����� �ѹ� ���� ����� �񱳵ż� �� �׽�Ʈ�� ������
		// �ѹ� ���� ���´� ó���� �����ϴٴ� ���� ���ؼ� �����ַ��� �־���.
		checkFindResult("SQL1", "SQL2", "SQL3");
		
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY99999923", "Modified9999");
		
		try {
			sqlRegistry.updateSql(sqlmap);
			//���ܰ� �߻��ؼ� catch ������� �Ѿ�� ������ ���� �߸��� ���̴�. �׶��� �׽�Ʈ�� ������ �����ϰ� ����� ���� �ٸ��� ������ ������ ã��
			fail();
		} catch(SqlUpdateFailureException e) {
			// ù ��° SQL�� ���������� ���������� Ʈ������� �ѹ�Ǳ� ������ �ٽ� ���� ���� ���·� ���ƿ;��Ѵ�.
			// Ʈ������� ������� �ʾҴٸ� ����� ä�� ���Ƽ� �׽�Ʈ�� ������ ���̴�.
			checkFindResult("SQL1", "SQL2", "SQL3");
		}
	}
	
	
}
