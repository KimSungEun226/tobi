package springbook.user.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.MockMailSender;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceImpl;
import springbook.user.service.UserServiceTx;

@RunWith(SpringJUnit4ClassRunner.class) //�������� �׽�Ʈ ���ؽ�Ʈ �����ӿ�ũ�� JUnit Ȯ���� ����
@ContextConfiguration(locations = "/test-applicationContext.xml") // �׽�Ʈ ���ؽ�Ʈ�� �ڵ����� ������� ���ø����̼� ���ؽ�Ʈ�� ��ġ ����
// �׽�Ʈ �޼ҵ忡�� ���ø����̼� ���ؽ�Ʈ�� �����̳� ���¸� �����Ѵٴ� ���� �׽�Ʈ ���ؽ�Ʈ �����ӿ�ũ�� �˷��ش�.
// �� ������̼��� ���� �׽�Ʈ Ŭ�������� ���ø����̼� ���ؽ�Ʈ ������ ������� �ʴ´�.
@DirtiesContext 
public class UserServiceTest {
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserServiceImpl userServiceImpl;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	PlatformTransactionManager transactionManager;
	
	@Autowired
	MailSender mailSender;
	
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
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		userService.upgradeLevels();
		
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
		
		List<String> requests = mockMailSender.getRequests();
		assertThat(requests.size(), is(2));
		assertThat(users.get(1).getEmail(), is(requests.get(0)));
		assertThat(users.get(1).getEmail(), is(requests.get(1)));
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
	public void upgradeAllOrNothing() throws Exception {
		TestUserService testUserService = new TestUserService(users.get(3).getId());
		testUserService.setUserDao(this.userDao);  //���� DI
		testUserService.setMailSender(mailSender);
		
		UserServiceTx txUserService = new UserServiceTx();
		txUserService.setTransactionManager(transactionManager);
		txUserService.setUserService(testUserService);
		
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try {
			txUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		}catch(TestUserServiceException e) {}
		
		checkLevelUpgraded(users.get(1), false);
	}
	
	private void checkLevelUpgraded(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		if (upgraded) assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		else assertThat(userUpdate.getLevel(), is(user.getLevel()));
	}
	
	static class TestUserService extends UserServiceImpl {
		private String id;
		
		private TestUserService(String id) {
			this.id = id;
		}
		
		protected void upgradeLevel(User user) {
			if (user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
	
	static class TestUserServiceException extends RuntimeException {}
}