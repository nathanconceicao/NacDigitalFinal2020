package br.com.fiap.EpicTask.service;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import br.com.fiap.EpicTask.model.User;
import br.com.fiap.EpicTask.repository.UserRepository;
import br.com.fiap.EpicTask.security.SecurityConfiguration;

@Service
public class UserService {

	@Autowired
	private UserRepository repository;

	public void update(User user) {
		repository.save(user);
	}

	public void create(User user) {
		user.setPass(SecurityConfiguration.passwordEncoder().encode(user.getPass()));
		repository.save(user);
	}

	public void delete(Long id) {
		Optional<User> user = repository.findById(id);
		if (user.isPresent()) {
			repository.deleteById(id);
		}else {
			throw new IllegalArgumentException("Usuário não existe");
		}
	}

	public User get(Long id) {
		Optional<User> user = repository.findById(id);
		if (user.isPresent()) {
			return user.get();
		}
		throw new IllegalArgumentException("Usuário não existe");
	}

	public ModelAndView sortAndOrder(Pageable pageable) throws Exception{
		
		Page<User> users = repository.findAll(pageable);
		
		String sort = users.getSort().stream()
						.map(order -> order.getProperty() + "," + order.getDirection())
						.collect(Collectors.joining(","));

		ModelAndView modelAndView = new ModelAndView("users");
		modelAndView.addObject("users", users);
		modelAndView.addObject("sort", sort);
		
		return modelAndView;
		
	}

	public ModelAndView sortAndOrderRanking(Pageable pageable) throws Exception{
		
		Page<User> users = repository.findAll(pageable);
		
		String sort = users.getSort().stream()
						.map(order -> order.getProperty() + "," + order.getDirection())
						.collect(Collectors.joining(","));

		ModelAndView modelAndView = new ModelAndView("users_ranking");
		modelAndView.addObject("users", users);
		modelAndView.addObject("sort", sort);
		
		return modelAndView;
		
	}

}
