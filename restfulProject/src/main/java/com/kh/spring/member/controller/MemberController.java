package com.kh.spring.member.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.spring.member.model.service.MemberService;
import com.kh.spring.member.model.vo.MemberVO;
import com.kh.spring.util.JWTUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/member")
@Slf4j
@Tag(name = "회원관련 컨트롤러", description = "회원 관련 전부")
public class MemberController {
	
	@Autowired
	private MemberService service;
	
	//BcryptPasswordEncoder 사용하기 위해서 스프링에게 주입 처리 하기 
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	@Autowired
	private JWTUtil jwtUtil;
	
	//회원가입
	@Operation(summary = "회원 가입", description = "회원 가입")
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
	
	//아이디 중복체크
	@Operation(summary = "아이디 중복 체크", description = "아이디 중복 체크")
	@GetMapping("/checkId/{loginId}")
	public ResponseEntity<?> idCheck(@PathVariable String loginId) {
		
		int count = service.checkId(loginId);
		
		//있으면(중복) 1 없으면 0 
		if(count>0) {
			
			return ResponseEntity.ok("NNNNN");
		}else {
			
			return ResponseEntity.ok("NNNNY");
		}
	}
	
	//로그인
	@Operation(summary = "로그인", description = "로그인")
	@PostMapping("/login")
	public ResponseEntity<?> loginMember(@RequestBody MemberVO m) {
		//post 요청시 json객체 형태로 데이터 전달하면 requestBody로 받아주어야함
		
		HashMap<String, Object> map = new HashMap<>();
		
		//사용자가 입력한 id로 회원 정보 조회
		MemberVO loginMember = service.loginMember(m);

		if(loginMember==null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
								 .body("존재하지 않는 회원입니다."); //이이디 잘못입력한경우
		}
		
		if(bcrypt.matches(m.getPassword(), loginMember.getPassword())) {
			
			//JWT 토큰 생성하여 응답데이터에 로그인 정보와 토큰정보 담아서 반환하기
			String token = jwtUtil.generateToken(loginMember.getLoginId());
			
			loginMember.setPassword(null);
			map.put("token", token);
			map.put("user", loginMember);
			
			return ResponseEntity.ok(map);
		}else { //비밀번호 오류
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					 .body("아이디 또는 비밀번호가 일치하지 않습니다."); //아이디 잘못입력한경우
		}
		
	}
	
	//로그아웃
	@Operation(summary = "로그아웃", description = "로그아웃")
	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		//JWT는 서버에서 세션을 관리하지 않기 때문에
		//클라이언트에서 토큰 삭제만으로 로그아웃처리 된다.
		
		return ResponseEntity.ok("로그아웃 되었습니다.");
	}
	
	//회원 정보 수정
	@Operation(summary = "회원 정보 수정", description = "회원 정보 수정")
	@PutMapping("/update")
	public ResponseEntity<?> updateMember(@RequestBody MemberVO m){
		
		int result = service.updateMember(m);
		
		if(result>0) {
			
			MemberVO loginMember = service.loginMember(m);
			
			//새로 조회한 정보 비밀번호 지우기
			loginMember.setPassword(null);
			
			return ResponseEntity.ok(loginMember);
			
		}else {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("회원수정 실패!");
			
		}
		
	}
	
	//회원탈퇴
	@Operation(summary = "회원 탈퇴", description = "회원 탈퇴")
	@DeleteMapping("/delete/{memberId}")
	public ResponseEntity<?> deleteMember(@PathVariable String memberId, @PathVariable String password){
		
		MemberVO m = MemberVO.builder().loginId(memberId).password(password).build();
		
		MemberVO loginMember = service.loginMember(m);
		
		if(loginMember != null && bcrypt.matches(m.getPassword(), loginMember.getPassword())) {
		
			int result = service.deleteMember(memberId);
			
			if(result>0) {
				
				return ResponseEntity.ok("회원 탈퇴 성공");
				
			}else {
				
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 탈퇴 실패");
				
			}
		}else {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("존재하지 않는 회원입니다.");
			
		}
	}
	
		
	
	
	
	
}
