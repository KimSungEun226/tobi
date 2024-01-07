package springbook;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailSender;

import springbook.user.service.DummyMailSender;
import springbook.user.service.UserService;
import springbook.user.test.UserServiceTest.TestUserService;

@Configuration
@Profile("test")
public class TestAppContext {
	
	@Bean
	public UserService testUserService() {
		TestUserService testUserService = new TestUserService();
		testUserService.setId("bbb4");
		return testUserService;
	}
	
	@Bean
	public MailSender mailSender() {
		return new DummyMailSender();
	}
}
