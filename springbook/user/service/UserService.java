package springbook.user.service;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import springbook.user.domain.User;

@Transactional
public interface UserService {
	void add(User user);
	
	@Transactional(readOnly = true)
	User get(String id);
	
	@Transactional(readOnly = true)
	List<User> getAll();
	
	void deleteAll();
	
	void update(User user);
	
	void upgradeLevels();
}
