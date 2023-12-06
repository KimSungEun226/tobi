package springbook.learningtest.jdk;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message> {

	// ������Ʈ�� ������ �� �ʿ��� ������ ���丮 ���� ������Ƽ�� �����ؼ� ��� DI ���� �� �ְ��Ѵ�.
	// ���Ե� ������ ������Ʈ ���� �߿� ���ȴ�.
	String text;
	
	public void setText(String text) {
		this.text = text;
	}
	
	// ���� ������ ���� ������Ʈ�� ���� �����Ѵ�.
	// �ڵ带 �̿��ϱ� ������ ������ ����� ������Ʈ ������ �ʱ�ȭ �۾��� �����ϴ�.
	@Override
	public Message getObject() throws Exception {
		return Message.newMessage(this.text);
	}
	
	@Override
	public Class<?> getObjectType() {
		return Message.class;
	}

	// getObject() �޼ҵ尡 �����ִ� ������Ʈ�� �̱������� �˷��ش�. �� ���丮 ���� �Ź� ��û�� ������ 
	// ���ο� ������Ʋ�� ����Ƿ� false�� �����Ѵ�. �̰��� ���丮 ���� ���۹�Ŀ� ���� �� ���̰� ������� �� ������Ʈ�� �̱������� �������� �������� �� �ִ�.
	@Override
	public boolean isSingleton() {
		return false;
	}
	
}
