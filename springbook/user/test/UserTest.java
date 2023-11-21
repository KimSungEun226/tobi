package springbook.user.test;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import springbook.user.dao.DaoFactory;
import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public class UserTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		ApplicationContext context = 
				new AnnotationConfigApplicationContext(DaoFactory.class);
		
		UserDao dao = context.getBean("userDao", UserDao.class);
		
		dao.deleteAll();
		
		User user = new User();
		user.setId("john");
		user.setName("�輺��");
		user.setPassword("1234");
		
		
		dao.add(user);
		
		System.out.println(user.getId() + "��� ����");
		
		User user2 = dao.get(user.getId());
		System.out.println(user2.getName());
		System.out.println(user2.getPassword());
		
		System.out.println(user2.getId() + "��ȸ ����");
	}
}
