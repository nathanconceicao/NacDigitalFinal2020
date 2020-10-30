package br.com.fiap.EpicTask.service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import br.com.fiap.EpicTask.model.Task;
import br.com.fiap.EpicTask.model.User;
import br.com.fiap.EpicTask.repository.TaskRepository;
import br.com.fiap.EpicTask.repository.UserRepository;

@Service
public class TaskService {

	@Autowired
	private TaskRepository repository;

	@Autowired
	private UserRepository userRepository;

	public void take(Long idTask, User user) {
		Optional<Task> task = repository.findById(idTask);
		if (task.isPresent()) {
			Task newTask = task.get();
			if (newTask.getUser() == null) {
				newTask.setUser(user);
				repository.save(newTask);
			}else {
				throw new IllegalArgumentException("Tarefa já tem um usuário atribuido");
			}
		}
	}

	public void update(Task task) {
		
		repository.save(task);
		if (task.getStatus()==100) {
			User user = task.getUser();
			int point = task.getPoint();
			user.toScore(point);
			userRepository.save(user);
		}
	}

	public void create(Task task) {
		
		repository.save(task);
		
	}

	public void delete(Long idTask) {
		Optional<Task> task = repository.findById(idTask);
		if (task.isPresent()) {
			repository.deleteById(idTask);
		}else {
			throw new IllegalArgumentException("Id não existe");
		}
	}

	public List<Task> findPendenting() {
		return repository.findByStatusLessThan(100);
	}

	public ModelAndView findCompleted(Pageable pageable) throws Exception{
		Page<Task> tasks = repository.findByStatusEquals(100, pageable);
		
	String sort = tasks.getSort().stream()
					.map(order -> order.getProperty() + "," + order.getDirection())
					.collect(Collectors.joining(","));

	ModelAndView modelAndView = new ModelAndView("tasks_completed");
	modelAndView.addObject("tasksCompleted", tasks);
	modelAndView.addObject("sort", sort);
	
	return modelAndView;
	
}

	public Task get(Long id) {
		Optional<Task> task = repository.findById(id);
		if (task.isPresent()) {
			return task.get();
		}
		throw new IllegalArgumentException("Id não existe");
	}

	public void drop(Long id, Principal auth) {
		Optional<Task> task = repository.findById(id);
		if (task.isPresent()) {
			Task newTask = task.get();
			if (newTask.getUser().getEmail().equals(auth.getName())) {
				newTask.setUser(null);
				repository.save(newTask);
			}else {
				throw new IllegalArgumentException("Tarefa não pertence a este usuário!");
			}
		}
	}

}
