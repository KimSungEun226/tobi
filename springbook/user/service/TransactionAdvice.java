package springbook.user.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionAdvice implements MethodInterceptor {

	private PlatformTransactionManager transactionManager; 	//Ʈ����� ����� �����ϴ� �� �ʿ��� Ʈ����� �Ŵ���
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			//�ݹ��� ȣ���ؼ� Ÿ���� �޼ҵ带 �����Ѵ�. Ÿ�� �޼ҵ� ȣ�� ���ķ� �ʿ��� �ΰ������ ���� �� �ִ�.
			// ��쿡 ���� Ÿ���� �ƿ� ȣ����� �ʰ� �ϰų� ��õ��� ���� �ݺ����� ȣ�⵵ �����ϴ�.
			Object ret = invocation.proceed();
			this.transactionManager.commit(status);
			return ret;
			
		//JDK ���̳��� ���Ͻð� �����ϴ� Method�ʹ� �޸� �������� MethodInvocation�� ���� Ÿ�� ȣ���� ���ܰ� ������� �ʰ� Ÿ�꿡�� ���� �״�� ����
		} catch (RuntimeException e) { 
			this.transactionManager.rollback(status);
			throw e;
		}
	}
}
