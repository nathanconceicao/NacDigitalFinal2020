package br.com.fiap.EpicTask.controller;

import java.sql.SQLException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

	@RequestMapping("/")
	public String home() throws SQLException, Exception{
		return "home";
	}
	
	@GetMapping("/login")
	public String login() throws SQLException, Exception{
		return "login";
	}
	
}
