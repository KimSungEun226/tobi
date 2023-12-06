package springbook.user.service;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

public class TxProxyFactoryBean implements FactoryBean<Object> {
	
	private Object target; 									//�ΰ������ ������ Ÿ�� ������Ʈ, � Ÿ���� ������Ʈ���� ���� �����ϴ�.
	private PlatformTransactionManager transactionManager; 	//Ʈ����� ����� �����ϴ� �� �ʿ��� Ʈ����� �Ŵ���
	private String pattern;
	// ���̳��� ���Ͻø� ������ �� �ʿ��ϴ�. 
	// UserService ���� �������̽��� ���� Ÿ�꿡�� ������ �� �ִ�.
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
	
	// FactoryBean ���� �޼ҵ�
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
		// ���丮 ���� �����ϴ� ������Ʈ Ÿ���� DI ���� �������̽� Ÿ�Կ� ���� �޶�����.
		// ���� �پ��� Ÿ���� ���Ͻ� ������Ʈ ������ ���� �� �� �ִ�.
		return serviceInterface;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

}
