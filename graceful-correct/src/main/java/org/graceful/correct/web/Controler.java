package org.graceful.correct.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class Controler {
	
	@RequestMapping("/user")
	public String test(Model model) {
		model.addAttribute("name", "test");
		model.addAttribute("id", 1);
		return "test";
	}
}
