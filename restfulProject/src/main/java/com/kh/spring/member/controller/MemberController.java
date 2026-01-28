package com.kh.spring.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.vo.MemberVO;
import com.kh.spring.util.JWTUtil;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/member")
@Slf4j
public class MemberController {
	
	@Autowired
	private MemberService service;
	
	//BcryptPasswordEncoder 사용하기 위해서 스프링에게 주입 처리 하기 
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Autowired
	private JWTUtil jwtUtil;
	
	//회원가입
	@PostMapping("/join")
	public ResponseEntity<?> insertMember(@RequestBody MemberVO m){
		
		m.setPassword(bcrypt.encode(m.getPassword()));
	
		int result = service.insertMember(m);
		
		if(result>0) {//회원가입 성공
			return ResponseEntity.status(HttpStatus.CREATED) //201
								 .body("회원가입이 완료되었습니다.");
		}else { //회원가입 실패
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) //500
					 .body("회원가입에 실패했습니다.");
		
		}
	}
	
	//로그인
	@PostMapping("/login")
	public ResponseEntity<?> loginMember(MemberVO m){
		
		MemberVO member = service.loginMember(m);
		
		return null;
	}
	
	//로그아웃
	
	
	//회원탈퇴
	
	
	//회원정지
	
	
	
	
	
	
	
	
	
	
	
}
