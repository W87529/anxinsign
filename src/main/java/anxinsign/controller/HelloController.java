package anxinsign.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
/**
* 第一个简单的例子HelloController
*
* @author administrator
**/
@Controller
public class HelloController {
@GetMapping(value = "/hello")
	public String hello(Model model) {
		String name = "测试";
		model.addAttribute("name", name);
		return "hello";
	}
}