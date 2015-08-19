package com.bankspring.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.bankspring.domain.MemberDto;
import com.bankspring.factory.Command;
import com.bankspring.factory.CommandFactory;
import com.bankspring.serviceImpl.MemberServiceImpl;

@Controller
@SessionAttributes("user")
@RequestMapping(value="/member")
public class MemberController {
	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	@Autowired MemberServiceImpl memberService;
	@Autowired MemberDto member;
	//******************************************
	//no execute
	//******************************************	
	@RequestMapping(value="/adminForm")
	public String adminForm(){
		logger.info("MemberController : adminForm()");
		
		return "admin/member/adminForm.tiles";
	}
	//******************************************
	//executeUpdate
	//******************************************
	@RequestMapping(value="/join",method=RequestMethod.POST)
	public String join(@ModelAttribute MemberDto member,
			ModelMap model ){
		logger.info("회원가입 아이디={}", member.getUserid());
		int joinOk = memberService.join(member);
		logger.info("회원가입 성공여부={}", joinOk);
		return "auth/home/main.tiles";
	}
	@RequestMapping(value="/update",method=RequestMethod.GET)
	public String updateForm(@ModelAttribute("user") MemberDto user,
			Model model){
		logger.info("수정페이지 이동");
		model.addAttribute("member",user);
		return "auth/member/update.tiles";
	}
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public String update(@ModelAttribute("user") MemberDto user,
			SessionStatus status,
			@RequestParam(value="email",required=false) String email,
			@RequestParam(value="password") String password,
			Model model){
		
		user.setEmail(email);
		user.setPassword(password);
		model.addAttribute("member",user);
		int result = memberService.update(user);
		logger.info("수정 후 결과보기 {}", result);
		String searchKey = "userid";
		String searchVal = user.getUserid();
		if (result != 0) { // update 를 성공했다면
			member = memberService.detail(CommandFactory.detail(searchKey, searchVal));
	    	model.addAttribute("user", member); // 세션에 업데이트된 정보를 담는다
			model.addAttribute("member",member);
			return "auth/member/detail.tiles";
		} else {
			return "auth/member/update.tiles";
		}
		
	}
	//******************************************
	//executeQuery
	//******************************************
	@RequestMapping("/detail")
	public String detail(@ModelAttribute("user") MemberDto user,
			Model model){
		logger.info("상세페이지 이동");
		model.addAttribute("member",user);
		return "auth/member/detail.tiles";
	}
	@RequestMapping("/mypage/{userid}")
	public @ResponseBody MemberDto myPage(@PathVariable("userid") String userid,
			Model model){
		logger.info("[마이페이지]");
		String searchKey = "userid",searchVal = userid;
		return memberService.detail(CommandFactory.detail(searchKey, searchVal));
	}
	@RequestMapping("/search/{userid}")
	public String search(@PathVariable("userid")String userid,
			Model model){
		logger.info("회원 상세페이지 검색");
		String searchKey = "userid",searchVal = userid;
		member = memberService.detail(CommandFactory.detail(searchKey, searchVal));
		model.addAttribute("member",member);
		return "auth/member/detail.tiles";
	}
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public String login(@RequestParam("userid")String userid,
			@RequestParam("password")String password,
			Model model){
		logger.info("[ 로그인 ] : id = {}, pass ={} ",userid, password);
		String searchKey = "userid",searchVal = userid;
		member = memberService.detail(CommandFactory.detail(searchKey, searchVal));
		if (member != null) {
			if (password.equals(member.getPassword())) {
				logger.info("===== 로그인 성공 =====");
				model.addAttribute("user", member);
				model.addAttribute("member",member);
				return "auth/content/book.tiles";
			} else {
				logger.info("===== 비밀번호가 일치하지 않습니다. =====");
				model.addAttribute("msg","비밀번호가 일치하지 않습니다.");
				return "auth/member/redirect.tiles";
			}
		} else {
			logger.info("===== 아이디가 존재하지 않습니다. =====");
			model.addAttribute("msg","아이디가 존재하지 않습니다.");
			return "auth/member/redirect.tiles";
		}
		
		
	}
	@RequestMapping("/logout")
	public String logout(@ModelAttribute("user") MemberDto user,
			SessionStatus status){
		logger.info("로그아웃 처리");
		status.setComplete(); // 세션을 비우고 로그아웃 처리
		return "public/home/main.tiles";
	}
	
	
	
}










