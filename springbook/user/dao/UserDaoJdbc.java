package springbook.user.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.Level;
import springbook.user.domain.User;

public class UserDaoJdbc implements UserDao{
	
	private JdbcTemplate jdbcTemplate;
	private RowMapper<User> userMapper = new RowMapper<User>() {
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new User(rs.getString("id"), 
					rs.getString("name"), 
					rs.getString("password"), 
					Level.valueOf(rs.getInt("level")),
					rs.getInt("login"),
					rs.getInt("recommend"));
		}
	};
		
	
	public void setDataSource(DataSource dataSource) {
		//�� ������Ʈ�� ������ �޼ҵ忡�� �ٸ� ������Ʈ�� �ʱ�ȭ�ϰ� �ڵ带 �̿��� DI�ϴ� ���� ������������ ���� ���Ǵ� ����̴�.
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	public void add(User user) {
		
		//�ݹ� ������Ʈ(StatementStragy)�� ������ ���� Ŭ������ Ŭ���̾�Ʈ �޼ҵ� ���� ����(user)�� ���� �����ϴ°͵� ���ø�/�ݹ��� ������ Ư¡
		this.jdbcTemplate.update("insert into users(id, name, password, level, login, recommend) values(?, ?, ?, ?, ?, ?)",
				user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend());
	}
	
	public User get(String id) {
		return this.jdbcTemplate.queryForObject("select * from users where id = ?", 
			new Object[] {id}, // SQL�� ���ε��� �Ķ���� ��, �������� ��� �迭�� ���
			userMapper);
	}
	
	public void deleteAll() {
//		this.jdbcTemplate.update(
//				new PreparedStatementCreator() {
//					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//						// TODO Auto-generated method stub
//						return con.prepareStatement("delete from users");
//					}
//				}
//			);
		this.jdbcTemplate.update("delete from users");
	}
	
	public int getCount() {
		return this.jdbcTemplate.query(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement("select count(*) from users");
			}
		}, new ResultSetExtractor<Integer>() {
			public Integer extractData(ResultSet rs) throws SQLException, DataAccessException{
				rs.next();
				return rs.getInt(1);
			}
		});
		//return this.jdbcTemplate.queryForInt("select count(*) from users");
	}


	public List<User> getAll() {
		return this.jdbcTemplate.query("select * from users order by id", userMapper);
	}


	@Override
	public void update(User user) {
		this.jdbcTemplate.update(
			"update users set name = ?, password = ?, level = ?, login =?, " + 
			"recommend = ? where id = ?", user.getName(), user.getPassword(),
			user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getId());
	}
}