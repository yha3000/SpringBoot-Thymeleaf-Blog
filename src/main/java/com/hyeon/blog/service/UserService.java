package com.hyeon.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hyeon.blog.model.RoleType;
import com.hyeon.blog.model.User;
import com.hyeon.blog.repository.UserRepository;

// 스프링이 컴포넌트 스캔을 통해서 Bean에 등록을 해줌 (IoC)
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Transactional
	public void save(User user) {
		String rawPassword = user.getPassword(); // 원문
		String encPassword = encoder.encode(rawPassword); // 해쉬
		user.setPassword(encPassword);
		user.setRole(RoleType.USER);
		userRepository.save(user);
	}
	
	@Transactional(readOnly = true)
	public User find(String username) {
		User user = userRepository.findByUsername(username).orElseGet(()->{
			return new User();
		});
		return user;
	}
	
	@Transactional
	public void update(User user) {
		// 수정시에는 영속성 컨텍스트 User 오브젝트를 영속화 시키고, 영속화된 오브젝트를 수정
		// select를 해서 User오브젝트를 DB로 부터 가져오는 이유는 영속화를 하기 위해서
		// 영속화된 오브젝트를 변경하면 자동으로 DB에 update문을 날려줌
		User persistance = userRepository.findById(user.getId()).orElseThrow(()->{
			return new IllegalArgumentException("회원 찾기 실패");
		});
		
		// Validate 체크 (OAuth로 회원가입하지 않은 사람만)
		if(persistance.getOauth() == null || persistance.getOauth().equals("")) {
			String rawPassword = user.getPassword(); // 원문
			String encPassword = encoder.encode(rawPassword); // 해쉬
			persistance.setPassword(encPassword);
			persistance.setEmail(user.getEmail());
		}
		
		// 회원 수정 함수 종료 = Service 종료 = Transaction 종료 = Commit이 자동으로 됨
		// 영속화된 persistance 객체의 변화가 감지되면 더티체킹이 되어 update문을 날려줌
	}
	
//	@Transactional(readOnly = true) // Select 할 때 트랜젝션 시작, 서비스 종료시에 트랜젝션 종료 (정합성)
//	public User login(User user) {
//			return userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
//	}
}
