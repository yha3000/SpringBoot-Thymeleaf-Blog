package com.hyeon.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.hyeon.blog.service.BoardService;

@Controller
public class BoardController {

	@Autowired
	private BoardService boardService;
	
	@GetMapping({"", "/"})
	public String index(Model model, @PageableDefault(size=3, sort="id", direction=Sort.Direction.DESC) Pageable pageable) {
//	컨트롤러에서 세션을 어떻게 찾는지
//	public String index(@AuthenticationPrincipal PrincipalDetail principal) {
//		System.out.println("로그인 사용자 아이디 : " + principal.getUsername());
//		WEB-INF/views/index.jsp
		model.addAttribute("boards", boardService.list(pageable));
		return "index"; // viewResolver 작동!!
	}
	
	@GetMapping("/board/{id}")
	public String findById(Model model, @PathVariable int id) {
		model.addAttribute("board", boardService.detail(id));
		return "board/detailForm";
	}
	
	@GetMapping("/board/{id}/updateForm")
	public String updateForm(Model model, @PathVariable int id) {
		model.addAttribute("board", boardService.detail(id));
		return "board/updateForm";
	}
	
	// User권한이 필요
	@GetMapping("/board/saveForm")
	public String saveForm() {
		return "board/saveForm";
	}	
	
}