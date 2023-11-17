package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CoursesController {
	@GetMapping("/courses")
//	@ResponseBody
	public String CoursesList(HttpServletRequest req) {
		HttpSession session = req.getSession();
		String cname = req.getParameter("cname");
		System.out.println("Selected course is: " + cname);
		session.setAttribute("cname", cname);
		System.out.println("Welcome message!!");	
		return "courses.jsp"; // convert jsp to servelet
	}
}
