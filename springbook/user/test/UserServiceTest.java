package springbook.user.test;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.MockMailSender;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class) //스프링의 테스트 컨텍스트 프레임워크의 JUnit 확장기능 지정
@ContextConfiguration(locations = "/test-applicationContext.xml") // 테스트 컨텍스트가 자동으로 만들어줄 애플리케이션 컨텍스트의 위치 지정
// 테스트 메소드에서 애플리케이션 컨텍스트의 구성이나 상태를 변경한다는 것을 테스트 컴텍스트 프레임워크에 알려준다.
// 이 어노테이션이 붙은 테스트 클래스에는 애플리케이션 컨텍스트 공유를 허용하지 않는다.
// @DirtiesContext 
// 롤백 여부에 대한 기본 설정과 트랜잭션 매니저 빈을 지정하는 데 사용할 수 있다.
// 디폴트 트랜잭션 매니저 아이디는 관례를 따라서 transactionManager로 되어 있다.
public class UserServiceTest {
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserService testUserService;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	ApplicationContext context; // 팩토리 빈을 가져오려면 애플리케이션 컨텍스트가 필요하다.
	
	List<User> users;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("kkk1", "김쿵캉", "k1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "zz@naber.com"),
				new User("aaa2", "이초난", "a2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "zz@naber.com"),
				new User("qqq3", "박냐츠", "q3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1, "zz@naber.com"),
				new User("bbb4", "최제츠", "b4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "zz@naber.com"),
				new User("nnn5", "나룻터", "n5", Level.GOLD, 100, 100, "zz@naber.com"));
	}
	
	@Test
	public void upgradeLevels() throws Exception{
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2));
		
		checkUserAndLevel(updated.get(0), "aaa2", Level.SILVER);
		checkUserAndLevel(updated.get(1), "bbb4", Level.GOLD);
		
		List<String> requests = mockMailSender.getRequests();
		assertThat(requests.size(), is(2));
		assertThat(users.get(1).getEmail(), is(requests.get(0)));
		assertThat(users.get(1).getEmail(), is(requests.get(1)));
	}
	
	@Test
	public void mockUpgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
	}
	
	@Test
	public void add() {
		userDao.deleteAll();
		
		User userWithLevel = users.get(4); //GOLD 레벨
		User userWithoutLevel = users.get(0); // 레벨이 비어 있는 사용자. 로직에 따라 등록중에 BASIC 레벨도 설정돼야 한다.
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(userWithoutLevel.getLevel()));
	}

	@Test
	@DirtiesContext //다이내믹 프록시 팩토리 빈을 직접 만들어 사용할 때는 없앴다가 다시 등장한 컨텍스트 무효화 애노테이션
	public void upgradeAllOrNothing() throws Exception {
		
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try {
			this.testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		}catch(TestUserServiceException e) {}
		
		checkLevelUpgraded(users.get(1), false);
	}
	
	@Test
	public void addInUpgrade() throws Exception {
		
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try {
			this.testUserService.add(null);
			fail("TestUserServiceException expected");
		}catch(TestUserServiceException e) {}
		
		checkLevelUpgraded(users.get(1), false);
	}
	
	@Test
	public void advisorAutoProxyCreator() {
		assertThat(testUserService, is(java.lang.reflect.Proxy.class));
	}
	
	@Test(expected=TransientDataAccessResourceException.class)
	public void readOnlyTransactionAttribute() {
		testUserService.getAll();
	}
	
	@Test
	public void transactionSync() {
		
		
		// 트랜잭션을 롤백했을 때 돌아갈 초기상태를 만들기 위해
		// 트랜잭션 시작 전에 초기화를 해둔다.
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));
		
		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
		// txDefinition.setReadOnly(true);
		
		TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);
		
		userService.add(users.get(0));
		userService.add(users.get(1));
		assertThat(userDao.getCount(), is(2));
		
		//강제로 롤백. 트랜잭션 시작 전 상태로 돌아가야한다.
		transactionManager.rollback(txStatus);
		
		assertThat(userDao.getCount(), is(0));
	}
	
	// 테스트용 트랜잭션 애노테이션은 테스트가 끝나면 자동으로 롤백된다.
	@Test()
	@Transactional()
	@Rollback(false) //롤백 어노테이션을 통한 커밋/롤백 설정가능
	public void transactionSyncByAnnotation() {
		userService.deleteAll();
		userService.add(users.get(0));
		userService.add(users.get(1));
	}
	
	private void checkLevelUpgraded(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		if (upgraded) assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		else assertThat(userUpdate.getLevel(), is(user.getLevel()));
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}
	
	// 포인컷의 클래스 필터에 선정되도록 이름 변경
	static class TestUserService extends UserServiceImpl {
		private String id;
		
		public void setId(String id) {
			this.id = id;
		}
		
		@Override
		public void add(User user) {
			// not supported 메소드내에서 호출된 메소드는 트랜잭션 처리가 가능할까?
			upgradeLevels();
			System.out.println("aaaaaaaaaaaaaaaaaaa" + this);
		}
		
		protected void upgradeLevel(User user) {
			if (user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
		
		public List<User> getAll() {
			for(User user : super.getAll()) {
				super.update(user);
			}
			return null;
		}
	}
	
	static class MockUserDao implements UserDao {
		
		private List<User> users; //레벨 업그레이드 후보 User 오브젝트 목록
		private List<User> updated = new ArrayList<User>(); // 업그레이드 대상 오브젝트를 저장해둘 목록
		
		private MockUserDao(List<User> users) {
			this.users = users;
		}
		
		public List<User> getUpdated() {
			return this.updated;
		}
		
		@Override
		public void add(User user) { throw new UnsupportedOperationException(); }

		@Override
		public User get(String id) { throw new UnsupportedOperationException(); }

		@Override
		public void deleteAll() { throw new UnsupportedOperationException(); }

		@Override
		public int getCount() { throw new UnsupportedOperationException(); }

		@Override
		public List<User> getAll() {
			return this.users; //스텁기능제공
		}
		
		@Override
		public void update(User user) {
			updated.add(user);  //목 오브젝트 기능 제공
		}
	}
	
	static class TestUserServiceException extends RuntimeException {}
}