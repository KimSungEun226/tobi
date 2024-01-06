package springbook.user.sqlservice;

public class DefaultSqlService extends BaseSqlService{
	
	public DefaultSqlService() {
		// �����ڿ��� ����Ʈ ���� ������Ʈ�� ���� ���� ������ DI���ش�.
		setSqlReader(new JaxbXmlSqlReader());
		setSqlRegistry(new HashMapSqlRegistry());
	}
}
