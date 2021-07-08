package com.hyeon.blog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyeon.blog.model.User;

// DAO
// 자동으로 bean 등록이 된다
// @Repository 생략 가능
public interface UserRepository extends JpaRepository<User, Integer>{

//	JPA Naming 쿼리 전략
//	SELECT * FROM user WHERE username = ?1 and password = ?2
//	User findByUsernameAndPassword(String user, String password);

//	@Query(value="SELECT * FROM user WHERE username = ?1 and password = ?2", nativeQuery = true)
//	User login(String username, String password);
	
	//SELECT * FROM user WHERE username = ?1
	Optional<User> findByUsername(String username);
	
}