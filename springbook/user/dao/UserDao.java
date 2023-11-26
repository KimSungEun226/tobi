package springbook.user.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

public class UserDao {
	
	private JdbcContext jdbcContext;
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		
		
		//한 오브젝트의 수정자 메소드에서 다른 오브젝트를 초기화하고 코드를 이용해 DI하는 것은 스프링에서도 종종 사용되는 기법이다.
		jdbcContext = new JdbcContext();
		jdbcContext.setDataSource(dataSource);
		
		this.dataSource = dataSource;
	}
	
	
	public void add(final User user) throws SQLException {
		
		
		//콜백 오브젝트(StatementStragy)를 구현한 내부 클래스가 클라이언트 메소드 내의 정보(user)를 직접 참조하는것도 템플릿/콜백의 고유한 특징
		this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {
			public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
				PreparedStatement ps = c.prepareStatement(
						"insert into users(id, name, password) values(?, ?, ?)");
				ps.setString(1, user.getId());
				ps.setString(2, user.getName());
				ps.setString(3, user.getPassword());
				return ps;
			}
		});
	}
	
	public User get(String id) throws SQLException {
		Connection c = dataSource.getConnection();
		PreparedStatement ps = c.prepareStatement(
				"select * from users where id = ?");
		ps.setString(1, id);
		
		ResultSet rs = ps.executeQuery();
		
		User user = null;
		
		if(rs.next()) {
			user = new User();
			user.setId(rs.getString("id"));
			user.setName(rs.getString("name"));
			user.setPassword(rs.getString("password"));			
		}
		
		rs.close();
		ps.close();
		c.close();
		
		if(user == null) throw new EmptyResultDataAccessException(1);
		
		return user;
	}
	
	public void deleteAll() throws SQLException {
		jdbcContext.executeSql("delete from users");
	}
	
	public int getCount() throws SQLException {
		
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			c = dataSource.getConnection();
			ps = c.prepareStatement(
					"select count(*) from users");
			
			rs = ps.executeQuery();
			rs.next();
			
			int count = rs.getInt(1);
			return count;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				}catch (SQLException e) {}
			}
			
			if (ps != null) {
				try {
					ps.close();
				}catch (SQLException e) {}
			}
			
			if (c != null) {
				try {
					c.close();
				}catch (SQLException e) {}
			}
			
		}
	}
}