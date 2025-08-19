package com.class_project.backend_class.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.class_project.backend_class.classes.Assunto;
import com.class_project.backend_class.repositories.AssuntoRepository;

@Service
public class AssuntoService {
	
	@Autowired
	private AssuntoRepository assuntoRepository;
	
	public List<Assunto> listarTodosOsAssuntos(){
		return assuntoRepository.findAll();
	}


	public Assunto buscarPorId(Long id) {
		Optional<Assunto> obj = assuntoRepository.findById(id);
		return obj.get();
	}
}
