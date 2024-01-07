package springbook.user.sqlservice;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import springbook.user.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class OxmSqlService implements SqlService{
	
	// SqlService�� ���� ���� �κ��� ������ ����� BaseSqlServcie�� �ν��Ͻ� ������ �����صд�.
	private final BaseSqlService baseSqlService = new BaseSqlService();
	
	private final OxmSqlReader oxmSqlReader = new OxmSqlReader();

	// oxmSqlReader�� �޸� ���� ����Ʈ ������Ʈ�� ������� ������Ƽ. ���� �ʿ信 ���� DI�� ���� ��ü ����
	private SqlRegistry sqlRegistry = new HashMapSqlRegistry();

	public void setSqlRegistry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.oxmSqlReader.setUnmarshaller(unmarshaller);
	}
	
	public void setSqlmap(Resource sqlmap) {
		this.oxmSqlReader.setSqlmap(sqlmap);
	}
	
	@PostConstruct
	public void loadSql() {
		// OxmSqlService�� ������Ƽ�� ���ؼ� �ʱ�ȭ�� SqlReader�� SqlRegistry�� ���� �۾��� ������ ����� baseSqlService�� ����
		this.baseSqlService.setSqlReader(oxmSqlReader);
		this.baseSqlService.setSqlRegistry(sqlRegistry);
		
		this.baseSqlService.loadSql();
	}
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		return this.baseSqlService.getSql(key);
	}
	
	// private ��� Ŭ������ �����Ѵ�. �鷹�� Ŭ������ OxmSqlService���� ����� �� �ִ�.
	private class OxmSqlReader implements SqlReader {
		
		private Unmarshaller unmarshaller;
		private Resource sqlmap = new ClassPathResource("sqlmap.xml", UserDao.class);
		
		public void setUnmarshaller(Unmarshaller unmarshaller) {
			this.unmarshaller = unmarshaller;
		}
		
		public void setSqlmap(Resource sqlmap) {
			this.sqlmap = sqlmap;
		};
		
		@Override
		public void read(SqlRegistry sqlRegistry) {
			try {
				Source source = new StreamSource(
					sqlmap.getInputStream());
				
				//OxmSqlService�� ���� ���޹��� OXM �������̽� ����������Ʈ�� ������ �𸶼��� �۾� ����
				Sqlmap sqlmap = (Sqlmap)this.unmarshaller.unmarshal(source);
				for(SqlType sql : sqlmap.getSql()) {
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
			} catch (IOException e) {
				throw new IllegalArgumentException(this.sqlmap.getFilename() + "�� ������ �� �����ϴ�.", e);
			}
			
		}	
	}
}
