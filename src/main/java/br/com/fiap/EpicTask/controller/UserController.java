package br.com.fiap.EpicTask.controller;

import java.sql.SQLException;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.EpicTask.exception.UserNotFoundException;
import br.com.fiap.EpicTask.model.User;
import br.com.fiap.EpicTask.repository.UserRepository;
import br.com.fiap.EpicTask.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private MessageSource messageSource;

	@Autowired
	private UserService service;

	@GetMapping()
	@Cacheable("users")
	public ModelAndView user(
			@PageableDefault(page = 0, size = 5, sort="name") Pageable pageable) throws SQLException, Exception {
		
		return service.sortAndOrder(pageable);
	}

	@GetMapping("/ranking")
	public ModelAndView userRanking(
			@PageableDefault(page = 0, size = 5, sort="point", direction = Direction.DESC) Pageable pageable) throws SQLException, Exception {
		
		return service.sortAndOrderRanking(pageable);
	}
	
	@RequestMapping("new")
	public String formNew(User user) throws SQLException, Exception{
		return "user_new";
	}
	
	@PostMapping()
	public String save(@Valid User user, BindingResult result, RedirectAttributes redirect) throws SQLException, Exception {
		if (result.hasErrors()) return "user_new";
		
		service.create(user);
		
		redirect.addFlashAttribute("message", getMessage("message.newuser.success"));
		return "redirect:/user";
	}
	
	@GetMapping("/delete/{id}")
	@CacheEvict(value="users", allEntries = true)
	public String deleteUser(@PathVariable Long id, RedirectAttributes redirect) throws SQLException, Exception {
		service.delete(id);
		redirect.addFlashAttribute("message", getMessage("message.deleteuser.success"));
		return "redirect:/user";
	}
	
	@GetMapping("/{id}")
	public ModelAndView editUserForm(@PathVariable Long id) throws SQLException, Exception {
		Optional<User> user = repository.findById(id);
		if (user.isEmpty()) throw new UserNotFoundException();
		ModelAndView modelAndView = new ModelAndView("user_edit");
		modelAndView.addObject("user", user);
		return modelAndView;		
	}
	
	@PostMapping("/update")
	public String updateUser(@Valid User user, BindingResult result, RedirectAttributes redirect) throws SQLException, Exception {
		if (result.hasErrors()) return "user_edit";
		service.update(user);
		redirect.addFlashAttribute("message", getMessage("message.edituser.success"));
		return "redirect:/user"; 
	}
	
	private String getMessage(String code) {
		return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
	}
	

}
