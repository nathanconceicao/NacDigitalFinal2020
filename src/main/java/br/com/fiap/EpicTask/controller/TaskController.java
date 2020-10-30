package br.com.fiap.EpicTask.controller;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.fiap.EpicTask.model.Task;
import br.com.fiap.EpicTask.model.User;
import br.com.fiap.EpicTask.repository.TaskRepository;
import br.com.fiap.EpicTask.service.TaskService;

@Controller
@RequestMapping("/task")
public class TaskController {
	
	@Autowired
	private TaskRepository repository;
	
	@Autowired
	private MessageSource messageSource;

	@Autowired
	private TaskService service;
	
	@GetMapping()
	public ModelAndView list() throws SQLException, Exception {
		List<Task> tasks = service.findPendenting();
		ModelAndView modelAndView = new ModelAndView("tasks");
		modelAndView.addObject("tasks", tasks);
		return modelAndView;
	}
	
	@GetMapping("/completed")
	public ModelAndView listCompleted(
			@PageableDefault(page = 0, size = 5) Pageable pageable) throws SQLException, Exception{
		return service.findCompleted(pageable);
	}
	
	@RequestMapping("new")
	public String formNew(Task task) throws SQLException, Exception{
		return "task_new";
	}
	
	@PostMapping()
	public String save(@Valid Task task, BindingResult result, RedirectAttributes redirect) throws SQLException, Exception {
		if (result.hasErrors()) return "task_new";
		service.create(task);
		redirect.addFlashAttribute("message", getMessage("message.newtask.success"));
		return "redirect:/task";
	}
	
	@GetMapping("/delete/{id}")
	public String deleteTask(@PathVariable Long id, RedirectAttributes redirect) throws SQLException, Exception {
		service.delete(id);
		redirect.addFlashAttribute("message", getMessage("message.deletetask.success"));
		return "redirect:/task";
	}
	
	@GetMapping("/{id}")
	public ModelAndView editUserForm(@PathVariable Long id) throws SQLException, Exception {
		Optional<Task> task = repository.findById(id);
		ModelAndView modelAndView = new ModelAndView("task_edit");
		modelAndView.addObject("task", task);
		return modelAndView;		
	}
	
	@PostMapping("/update")
	public String update(@Valid Task task, BindingResult result, RedirectAttributes redirect) throws SQLException, Exception {
		if (result.hasErrors()) return "task_edit";
		service.update(task);
		redirect.addFlashAttribute("message", getMessage("message.edittask.success"));
		return "redirect:/task"; 
	}
	
	@GetMapping("/take/{id}")
	public String take(@PathVariable Long id, Authentication auth) throws SQLException, Exception {
		User user = (User) auth.getPrincipal();
		service.take(id, user);
		return "redirect:/task"; 
	}
	
	@GetMapping("/drop/{id}")
	public String drop(@PathVariable Long id, Principal auth) throws SQLException, Exception {
		service.drop(id, auth);
		return "redirect:/task"; 
	}
	
	private String getMessage(String code) {
		return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
	}
	
	
}
