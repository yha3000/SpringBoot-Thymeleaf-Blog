package com.hyeon.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hyeon.blog.model.Board;

public interface BoardRepository extends JpaRepository<Board, Integer>{

}