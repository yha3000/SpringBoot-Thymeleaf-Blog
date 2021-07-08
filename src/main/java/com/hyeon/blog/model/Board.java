package com.hyeon.blog.model;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
	private int id;
	
	@Column(nullable = false, length = 100)
	private String title;
	
	@Lob// 대용량 데이터
	private String content; // 섬머노트 라이브러리 <html>태그가 섞여서 디자인이 됨.
	
//	@ColumnDefault("0")
	private int count; // 조회수
	
	// Board = Many, User = One -> 하나의 유저는 여러개의 게시글을 쓸 수 있다.
	// @ManyToOne, @OneToOne 어노테이션들은 기본이 즉시 로딩(EAGER) 이다.
	// 처음 엔티티를 조회할 때 값을 가지고 온다.
	// @OneToMany, @ManyToMany 어노테이션들은 기본이 지연 로딩(LAZY)이다.
	// .get() 메소드를 실행할 때 쿼리를 실행하여 값을 가져온다.
	@ManyToOne(fetch = FetchType.EAGER) 
	@JoinColumn(name="userId")
	private User user; // DB는 오브젝트를 저장할 수 없다. FK, 자바는 오브젝트를 저장할 수 있다.
	
	// Board = One, Reply = Many
	// mappedBy 연관관계의 주인이 아니다 (난 FK가 아니에요)
	// cascade = CascadeType.REMOVE (게시글을 지울때 댓글도 다 지우겠다)
	@OneToMany(mappedBy = "board", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE) 
	// 무시할 속성이나 속성 목록을 표시하는데 사용됨
	// 무한 참조 방지
	@JsonIgnoreProperties({"board"})
	@OrderBy("id desc")
	private List<Reply> replys;
	
	@CreationTimestamp
	private Timestamp createDate;
}