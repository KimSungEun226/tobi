package springbook.user.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

public class UserDao {
	
	private DataSource dataSource;
	
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public void add(User user) throws SQLException {
		jdbcContextWithStatementStrategy(new AddStatement(user));
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
		jdbcContextWithStatementStrategy(new DeleteAllStatement());
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
	
	public void jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
		Connection c = null;
		PreparedStatement ps = null;
		
		try {
			c = dataSource.getConnection();
			ps = stmt.makePreparedStatement(c);
			ps.executeUpdate();
		}catch (SQLException e) {
			throw e;
		} finally {
			if(ps != null) {
				try {
					ps.close();
				}catch (SQLException e) {
					
				}
			}
			if (c != null) {
				try {
					c.close();
				}catch (SQLException e) {
					
				}
			}			 
		}
		
	}
}