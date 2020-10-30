package br.com.fiap.EpicTask.exception;

import java.security.Principal;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalHandlerException {
	
	@ExceptionHandler({SQLException.class, DataAccessException.class, Exception.class})
	public ModelAndView handleDatabaseError(Exception ex, HttpServletRequest req, Principal user) {
		ModelAndView modelAndView = null;
		Logger log = LoggerFactory.getLogger(GlobalHandlerException.class);
		log.error("Erro= " + ex.getClass() + 
				" url= " + req.getRequestURL() +
				" user= " + user.getName());
		if(ex.getClass().equals(SQLException.class) || ex.getClass().equals(DataAccessException.class)) {
			modelAndView = new ModelAndView("database_error");
		}else {
			modelAndView = new ModelAndView("error");
		}
		modelAndView.addObject("error", ex.getClass());
		modelAndView.addObject("message", ex.getMessage());
		
		return modelAndView;
	}
	
	
	
}
