package springbook.learningtest.jdk.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class DynamicProxyTest {
	
	@Test
	public void pointcutAdvisor() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		
		// �޼ҵ� �̸��� ���ؼ� ����� �����ϴ� �˰����� �����ϴ� ����Ʈ�� ����
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*"); //�̸� ������ ����, sayH�� �����ϴ� ��� �޼ҵ带 �����ϰ� �Ѵ�.
		
		// ����Ʈ�ư� �����̽��� Advisor�� ��� �� ���� �߰�
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice())); 
		
		Hello proxiedHello = (Hello) pfBean.getObject(); // FactoryBean �̹Ƿ� getObject()�� ������ ���Ͻø� �����´�.
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
	}
	
	@Test
	public void proxyFactoryBean() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());		// Ÿ�� ����
		pfBean.addAdvice(new UppercaseAdvice());	// �ΰ������ ���� �����̽��� �߰��Ѵ�. �������� �߰��� ���� �ִ�.
		
		Hello proxiedHello = (Hello) pfBean.getObject(); // FactoryBean �̹Ƿ� getObject()�� ������ ���Ͻø� �����´�.
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
	
	@Test
	public void classNamePointcutAdvisor() {
		// ����Ʈ �� �غ�
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
			public ClassFilter getClassFilter() { //�͸� ���� Ŭ���� ������� Ŭ���� ����
				return new ClassFilter() {
					
					@Override
					public boolean matches(Class<?> clazz) {
						return clazz.getSimpleName().startsWith("HelloT");
					}
				};
			}
		};
		classMethodPointcut.setMappedName("sayH*");
		
		// �׽�Ʈ
		checkAdviced(new HelloTarget(), classMethodPointcut, true);
		
		class HelloWorld extends HelloTarget {};
		checkAdviced(new HelloWorld(), classMethodPointcut, false);
		
		class HelloToby extends HelloTarget {};
		checkAdviced(new HelloToby(), classMethodPointcut, true);
		
	}
	
	private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		if (adviced) {
			assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
			assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
			assertThat(proxiedHello.sayThankYou("Toby"), is("Thank You Toby"));
		}
	}
	
	static class UppercaseAdvice implements MethodInterceptor {
		public Object invoke(MethodInvocation invocation) throws Throwable {
			// ���÷����� Method�� �޸� �޼ҵ� ���� �� Ÿ�� ������Ʈ�� ������ �ʿ䰡 ����.
			// MethodInvocation�� �޼ҵ� ������ �Բ� Ÿ�� ������Ʈ�� �˰� �ֱ� �����̴�.
			String ret = (String)invocation.proceed();
			return ret.toUpperCase();
		}
	}
	
	static interface Hello { //Ÿ��� ���Ͻð� ������ �������̽�
		String sayHello(String name);
		String sayHi(String name);
		String sayThankYou(String name);
	}
	
	static class HelloTarget implements Hello { //Ÿ�� Ŭ����
		public String sayHello(String name) { return "Hello " + name; }
		public String sayHi(String name) { return "Hi " + name; }
		public String sayThankYou(String name) { return "Thank You " + name; }
	}
}
