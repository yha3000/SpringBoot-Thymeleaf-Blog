package com.hyeon.blog.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hyeon.blog.dto.ResponseDto;
import com.hyeon.blog.model.User;
import com.hyeon.blog.service.UserService;

@RestController 
public class UserApiController {
	
	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationManager authenticationManager;
		
//	@PostMapping("/api/user")
	@PostMapping("/auth/joinProc") // 시큐리티
	public ResponseDto<Integer> save(@RequestBody User user) { // username, password, email
		// 실제로 DB에 insert를 하고 아래에서 return이 되면 된다.
		userService.save(user);
		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1); // 자바오브젝트를 JSON으로 변환해서 리턴 (JackSon)
	}
	
	@PutMapping("/user/updateProc")
//	public ResponseDto<Integer> update(@RequestBody User user, @AuthenticationPrincipal PrincipalDetail principal, HttpSession session) {
	public ResponseDto<Integer> update(@RequestBody User user) { // id, password, email // key=value, x-www-form-urlencoded
		userService.update(user);
		
		// 여기서는 트랜잭션이 종료되기 때문에 DB의 값은 변경이 됐음
		// 하지만 세션값은 변경되지 않은 상태이기 때문에 직접 세션값을 변경해야함
//		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
//		SecurityContext securityContext = SecurityContextHolder.getContext();
//		securityContext.setAuthentication(authentication);
//		session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
		
		// 세션 등록
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
	}
	
//	전통적인 로그인 방법
//	@Autowired
//	private HttpSession session;
//	@PostMapping("/api/user/login")
//	public ResponseDto<Integer> login(@RequestBody User user) {
//		System.out.println("UserApiController : login 호출됨");
//		User principal = userService.login(user); // principal (접근주체)
//		if(principal != null) {
//			session.setAttribute("principal", principal);
//		}
//		return new ResponseDto<Integer>(HttpStatus.OK.value(), 1);
//	}
}