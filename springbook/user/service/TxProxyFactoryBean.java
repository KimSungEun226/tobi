package springbook.user.service;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

public class TxProxyFactoryBean implements FactoryBean<Object> {
	
	private Object target; 									//부가기능을 제공할 타깃 오브젝트, 어떤 타입의 오브젝트에도 적용 가능하다.
	private PlatformTransactionManager transactionManager; 	//트랜잭션 기능을 제공하는 데 필요한 트랜잭션 매니저
	private String pattern;
	// 다이내믹 프록시를 생성할 때 필요하다. 
	// UserService 외의 인터페이스를 가진 타깃에도 적용할 수 있다.
	Class<?> serviceInterface;
	
	public void setTarget(Object target) {
		this.target = target;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}
	
	// FactoryBean 구현 메소드
	@Override
	public Object getObject() throws Exception {
		
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTransactionManager(transactionManager);
		txHandler.setTarget(target);
		txHandler.setPattern(pattern);
		return Proxy.newProxyInstance(getClass().getClassLoader(), 
				new Class[] { serviceInterface }, txHandler);
		
	}

	@Override
	public Class<?> getObjectType() {
		// 팩토리 빈이 생성하는 오브젝트 타입은 DI 받은 인터페이스 타입에 따라 달라진다.
		// 따라서 다양한 타입의 프록시 오브젝트 생성에 재사용 할 수 있다.
		return serviceInterface;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
