package springbook.user.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.AppContext;
import springbook.TestAppContext;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

@RunWith(SpringJUnit4ClassRunner.class) //스프링의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
@ContextConfiguration(classes = {TestAppContext.class, AppContext.class}) // 테스트 컨텍스트가 자동으로 만들어줄 애플리케이션 컨텍스트의 위치 지정
// 테스트 메소드에서 애플리케이션 컨텍스트의 구성이나 상태를 변경한다는 것을 테스트 컴텍스트 프레임워크에 알려준다.
// 이 어노테이션이 붙은 테스트 클래스에는 애플리케이션 컨텍스트 공유를 허용하지 않는다.
// @DirtiesContext 
public class UserDaoTest {
	
	
	@Autowired
	private UserDao dao;
	@Autowired
	DataSource dataSource;
	private User user1;
	private User user2;
	private User user3;
	
	@Before
	public void setUp() {
		
//		dao = new UserDao();
//		DataSource dataSource = new SingleConnectionDataSource(
//				"jdbc:mysql://localhost:3306/tobi-spring", "tobi", "1234", true);
//		dao.setDataSource(dataSource);
		
		this.user1 = new User("zz1", "김덕배", "duckbae11", Level.BASIC, 1, 0, "asd@nav.com");
		this.user2 = new User("zz2", "창김화", "kim123", Level.SILVER, 55, 10, "asd@nav.com");
		this.user3 = new User("zz3", "송아담", "song344", Level.GOLD, 100, 40, "asd@nav.com");
	}
	
	@Test
	public void addAndGet() throws SQLException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		User userget1 = dao.get(user1.getId());
		checkSameUser(userget1, user1);
		
		User userget2 = dao.get(user2.getId());
		checkSameUser(userget2, user2);
	}
	
	@Test
	public void count() throws SQLException {
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
	}
	
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {

		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("unknown_id");
		
	}
	
	@Test
	public void getAll() throws SQLException{
		dao.deleteAll();
		
		List<User> users0 = dao.getAll();
		assertThat(users0.size(), is(0));
		
		dao.add(user1);
		List<User> users1 = dao.getAll();
		assertThat(users1.size(), is(1));
		checkSameUser(user1, users1.get(0));
		
		dao.add(user2);
		List<User> users2 = dao.getAll();
		assertThat(users2.size(), is(2));
		checkSameUser(user1, users2.get(0));
		checkSameUser(user2, users2.get(1));
		
		dao.add(user3);
		List<User> users3 = dao.getAll();
		assertThat(users3.size(), is(3));
		checkSameUser(user1, users3.get(0));
		checkSameUser(user2, users3.get(1));
		checkSameUser(user3, users3.get(2));
	}

	private void checkSameUser(User user1, User user2) {
		assertThat(user1.getId(), is(user2.getId()));
		assertThat(user1.getName(), is(user2.getName()));
		assertThat(user1.getPassword(), is(user2.getPassword()));
		assertThat(user1.getLevel(), is(user2.getLevel()));
		assertThat(user1.getLogin(), is(user2.getLogin()));
		assertThat(user1.getRecommend(), is(user2.getRecommend()));
		assertThat(user1.getEmail(), is(user2.getEmail()));
	}
	
	@Test(expected = DuplicateKeyException.class)
	public void duplicateKey() {
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user1);
	}
	
	@Test
	public void sqlExceptionTranslate() {
		dao.deleteAll();
		
		try {
			dao.add(user1);
			dao.add(user1);
		}catch (DuplicateKeyException ex) {
			SQLException sqlEx = (SQLException)ex.getRootCause();
			SQLExceptionTranslator set = 
					new SQLErrorCodeSQLExceptionTranslator(this.dataSource);
			assertThat(set.translate(null, null, sqlEx), is(DuplicateKeyException.class));
		}
	}
	
	@Test
	public void update() {
		dao.deleteAll();
		
		dao.add(user1);
		dao.add(user2);
		
		user1.setName("죠나단");
		user1.setPassword("ddd");
		user1.setLevel(Level.GOLD);
		user1.setLogin(1000);
		user1.setRecommend(999);
		user1.setEmail("asx@daum.cme");
		dao.update(user1);
		
		User user1update = dao.get(user1.getId());
		checkSameUser(user1, user1update);
		User user2same = dao.get(user2.getId());
		checkSameUser(user2, user2same);
		
	}
}
