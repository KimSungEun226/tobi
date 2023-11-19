package springbook.user.test;

import java.sql.SQLException;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public class UserTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		UserDao dao = new UserDao();
		
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
