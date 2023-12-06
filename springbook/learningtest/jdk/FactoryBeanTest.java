package springbook.learningtest.jdk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FactoryBeanTest {
	
	@Autowired
	ApplicationContext context;
	
	@Test 
	public void getMessageFromFactoryBean() {
		
		// &가 붙고 안 붙고에 따라 getBean() 메소드가 돌려주는 오브젝트가 달라진다.
		Object factory = context.getBean("&message");
		Object message = context.getBean("message");
		assertThat(message, is(Message.class));
		assertThat(factory, is(MessageFactoryBean.class));
		assertThat(((Message)message).getText(), is("Factory Bean"));
	}
}
