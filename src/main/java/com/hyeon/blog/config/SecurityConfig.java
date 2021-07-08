package com.hyeon.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.hyeon.blog.config.auth.PrincipalDetailService;

@Configuration // 빈등록 (IoC관리) : 스프링 컨테이너에서 객체를 관리할 수 있게 하는 것.
@EnableWebSecurity // 시큐리티 필터 추가 = 스프링 시큐리티가 활성화가 되어있는데 설정을 해당 파일에서 하겠다.
@EnableGlobalMethodSecurity(prePostEnabled = true) // 특정 주소로 접근을 하면 권한 및 인증을 미리 체크하겠다는 뜻.
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private PrincipalDetailService principalDetailService;
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean // IoC가 된다 (변경된 해쉬값을 스프링이 관리함)
	public BCryptPasswordEncoder encodePWD() {
		return new BCryptPasswordEncoder();
	}
	
	// 시큐리티가 대신 로그인해주는데 password를 가로채기를 하는데
	// 해당 password가 뭘로 해쉬가 되어 회원가입이 되어있는지 알아야
	// 같은 해쉬로 암호화해서 DB에 있는 해쉬랑 비교할 수 있음.
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(principalDetailService).passwordEncoder(encodePWD());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable() // csrf 토큰 비활성화 (테스트시 걸어두는 게 좋음)
			.authorizeRequests() // request가 들어올 때
//			.antMatchers("/auth/loginForm", "/auth/joinForm")
			.antMatchers("/", "/auth/**", "/js/**", "/css/**", "/image/**") // 여기 설정 된 주소로 request가 온다면
			.permitAll() // 누구나 들어갈 수 있음
			.anyRequest() // 위의 설정 된 주소이외에 다른 주소로 request가 오면
			.authenticated() // 인증이 되어야 함.
		.and() // 그리고
			.formLogin() // 인증이 필요한 로그인창은
			.loginPage("/auth/loginForm") // 해당 페이지를 보여준다.
			.loginProcessingUrl("/auth/loginProc") // 스프링 시큐리티가 해당 주소로 요청이 오는 로그인을 가로채서 대신 로그인 해준다.
			.defaultSuccessUrl("/") // 로그인 성공 후 이동할 페이지
//			.failureUrl("/auth/loginForm") // 실패 했을 때 이동할 페이지
		;
	}
	
}