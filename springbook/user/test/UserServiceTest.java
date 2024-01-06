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

@RunWith(SpringJUnit4ClassRunner.class) //�������� �׽�Ʈ ���ؽ�Ʈ �����ӿ�ũ�� JUnit Ȯ���� ����
@ContextConfiguration(locations = "/test-applicationContext.xml") // �׽�Ʈ ���ؽ�Ʈ�� �ڵ����� ������� ���ø����̼� ���ؽ�Ʈ�� ��ġ ����
// �׽�Ʈ �޼ҵ忡�� ���ø����̼� ���ؽ�Ʈ�� �����̳� ���¸� �����Ѵٴ� ���� �׽�Ʈ ���ؽ�Ʈ �����ӿ�ũ�� �˷��ش�.
// �� ������̼��� ���� �׽�Ʈ Ŭ�������� ���ø����̼� ���ؽ�Ʈ ������ ������� �ʴ´�.
// @DirtiesContext 
// �ѹ� ���ο� ���� �⺻ ������ Ʈ����� �Ŵ��� ���� �����ϴ� �� ����� �� �ִ�.
// ����Ʈ Ʈ����� �Ŵ��� ���̵�� ���ʸ� ���� transactionManager�� �Ǿ� �ִ�.
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
	ApplicationContext context; // ���丮 ���� ���������� ���ø����̼� ���ؽ�Ʈ�� �ʿ��ϴ�.
	
	List<User> users;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("kkk1", "����Ĳ", "k1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "zz@naber.com"),
				new User("aaa2", "���ʳ�", "a2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "zz@naber.com"),
				new User("qqq3", "�ڳ���", "q3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1, "zz@naber.com"),
				new User("bbb4", "������", "b4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "zz@naber.com"),
				new User("nnn5", "������", "n5", Level.GOLD, 100, 100, "zz@naber.com"));
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
		
		User userWithLevel = users.get(4); //GOLD ����
		User userWithoutLevel = users.get(0); // ������ ��� �ִ� �����. ������ ���� ����߿� BASIC ������ �����ž� �Ѵ�.
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(userWithoutLevel.getLevel()));
	}

	@Test
	@DirtiesContext //���̳��� ���Ͻ� ���丮 ���� ���� ����� ����� ���� ���ݴٰ� �ٽ� ������ ���ؽ�Ʈ ��ȿȭ �ֳ����̼�
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
		
		
		// Ʈ������� �ѹ����� �� ���ư� �ʱ���¸� ����� ����
		// Ʈ����� ���� ���� �ʱ�ȭ�� �صд�.
		userDao.deleteAll();
		assertThat(userDao.getCount(), is(0));
		
		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
		// txDefinition.setReadOnly(true);
		
		TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);
		
		userService.add(users.get(0));
		userService.add(users.get(1));
		assertThat(userDao.getCount(), is(2));
		
		//������ �ѹ�. Ʈ����� ���� �� ���·� ���ư����Ѵ�.
		transactionManager.rollback(txStatus);
		
		assertThat(userDao.getCount(), is(0));
	}
	
	// �׽�Ʈ�� Ʈ����� �ֳ����̼��� �׽�Ʈ�� ������ �ڵ����� �ѹ�ȴ�.
	@Test()
	@Transactional()
	@Rollback(false) //�ѹ� ������̼��� ���� Ŀ��/�ѹ� ��������
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
	
	// �������� Ŭ���� ���Ϳ� �����ǵ��� �̸� ����
	static class TestUserService extends UserServiceImpl {
		private String id;
		
		public void setId(String id) {
			this.id = id;
		}
		
		@Override
		public void add(User user) {
			// not supported �޼ҵ峻���� ȣ��� �޼ҵ�� Ʈ����� ó���� �����ұ�?
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
		
		private List<User> users; //���� ���׷��̵� �ĺ� User ������Ʈ ���
		private List<User> updated = new ArrayList<User>(); // ���׷��̵� ��� ������Ʈ�� �����ص� ���
		
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
			return this.users; //���ӱ������
		}
		
		@Override
		public void update(User user) {
			updated.add(user);  //�� ������Ʈ ��� ����
		}
	}
	
	static class TestUserServiceException extends RuntimeException {}
}