package com.hyeon.blog.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyeon.blog.model.KakaoProfile;
import com.hyeon.blog.model.OAuthToken;
import com.hyeon.blog.model.User;
import com.hyeon.blog.service.UserService;

// 인증이 안된 사용자들이 출입할 수 있는 경로를 /auth/** 허용
// 그냥 주소가 / 이면 index.jsp 허용
// resources/static 이하에 있는 /js/**, /css/**, /image/** 허용 
@Controller
public class UserController {

	// yml에서 만든 Key 사용
	@Value("${hyeon.key}")
	private String hyeonKey;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/auth/joinForm") // 시큐리티
//	@GetMapping("/user/joinForm")
	public String joinForm() {
		return "user/joinForm";
	}
	
	@GetMapping("/auth/loginForm") // 시큐리티
//	@GetMapping("/user/loginForm")
	public String loginForm() {
		return "user/loginForm";
	}
	
	@GetMapping("/auth/kakao/callback")
	public String kakaoCallback(String code) { // Data를 리턴해주는 컨트롤러 함수
		// HTML의 <a> Tag는 Get 방식임
		// POST방식으로 Key=value 데이터를 요청 (카카오쪽으로)
		// Retrofit2(안드로이드), OkHttp, RestTemplate
		
		// AccessToken 요청
		RestTemplate rt = new RestTemplate();
		
		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "a399eec9ddf1c5360e2cdf340cd02162");
		params.add("redirect_uri", "http://localhost:8000/auth/kakao/callback");
		params.add("code", code);
		
		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
		
		// Http 요청하기 - Post방식으로 - 그리고 response 변수에 응답받음
		ResponseEntity<String> response = rt.exchange(
				"https://kauth.kakao.com/oauth/token"
				, HttpMethod.POST
				, kakaoTokenRequest
				, String.class
		);
		
		// Gson, Json Simple, ObjectMapper
		ObjectMapper obMapper = new ObjectMapper();
		OAuthToken oauthToken = null;
		try {
			oauthToken = obMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}	
		
		// 사용자 정보 요청
		RestTemplate rt2 = new RestTemplate();
		
		// HttpHeader 오브젝트 생성
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers2);
		
		// Http 요청하기 - Post방식으로 - 그리고 response 변수에 응답받음
		ResponseEntity<String> response2 = rt2.exchange(
				"https://kapi.kakao.com/v2/user/me"
				, HttpMethod.POST
				, kakaoProfileRequest
				, String.class
		);
		
		// Gson, Json Simple, ObjectMapper
		ObjectMapper obMapper2 = new ObjectMapper();
		KakaoProfile kakaoProfile = null;
		try {
			kakaoProfile = obMapper2.readValue(response2.getBody(), KakaoProfile.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}	
				
		// User Object는 username, password, email가 필요함
//		System.out.println("카카오 아이디(번호) : " + kakaoProfile.getId());
//		System.out.println("카카오 이메일 : " + kakaoProfile.getKakao_account().getEmail());
//		System.out.println("블로그서버 유저네임 : " + kakaoProfile.getKakao_account().getEmail() + "_" + kakaoProfile.getId());
//		System.out.println("블로그서버 패스워드 : " + hyeonKey);
//		System.out.println("블로그서버 이메일 : " + kakaoProfile.getKakao_account().getEmail());

		// UUID -> 중복되지 않는 어떤 특정 값을 만들어내는 알고리즘
//		UUID garbegePassword = UUID.randomUUID();
		
		User kakaoUser = User.builder()
				.username(kakaoProfile.getKakao_account().getEmail() + "_" + kakaoProfile.getId())
				.password(hyeonKey)
				.email(kakaoProfile.getKakao_account().getEmail())
				.oauth("kakao")
				.build();
		
		// 가입자 혹은 비가입자 체크해서 처리
		User originUser = userService.find(kakaoUser.getUsername());
		// 비가입자인 경우 회원가입
		if(originUser.getUsername() == null)  {
//			System.out.println("기존 회원이 아니기에 자동 회원가입을 진행합니다.");
			userService.save(kakaoUser);
		}
		// 로그인 처리
//		System.out.println("자동로그인을 진행합니다.");
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), hyeonKey));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return "redirect:/";
	}
	
	@GetMapping("/user/updateForm")
	public String updateForm(@AuthenticationPrincipal Principal principal) {
		return "user/updateForm";
	}

}