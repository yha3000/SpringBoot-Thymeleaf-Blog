package com.hyeon.blog.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // 빌더 패턴
@Entity // User 클래스가 MySQL에 테이블이 생성이 된다  //ORM -> Java(다른언어) Object -> 테이블로 매핑해주는 기술
// @DynamicInsert // null인 값은 제외하고 Insert 해 쥼
public class User {
	
	@Id // Primary Key
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 프로젝트에서 연결된 DB의 넘버링 전략을 따라간다
	private int id; //시퀀스, auto_increment
	
	@Column(nullable = false, length = 100, unique = true)
	private String username; // id
	
	@Column(nullable = false, length = 100) // 123456 => 해쉬 (비밀번호 암호화)
	private String password;
	
	@Column(nullable = false, length = 50)
	private String email;
	
	// DB는 RoleType이 없다
	//	@ColumnDefault("'user'")
	@Enumerated(EnumType.STRING)
	private RoleType role; // Enum을 쓰는게 좋다 // ADMIN, USER
	
	private String oauth; // kakao, google.. 등등
	
	@CreationTimestamp // 시간이 자동으로 입력
	private Timestamp createDate;
}