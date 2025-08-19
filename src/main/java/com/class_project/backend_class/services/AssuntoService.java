package com.class_project.backend_class.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.class_project.backend_class.classes.Assunto;
import com.class_project.backend_class.repositories.AssuntoRepository;

@Service
public class AssuntoService {
	
	@Autowired
	private AssuntoRepository assuntoRepository;
	
	@Cacheable(value = "assuntos")
	public List<Assunto> listarTodosOsAssuntos(){
		System.out.println("Buscando assuntos no banco de dados");
		return assuntoRepository.findAll();
	}
	
	@Cacheable(value = "assunto", key = "#id")
	public Assunto buscarPorId(Long id) {
		Optional<Assunto> obj = assuntoRepository.findById(id);
		return obj.orElseThrow(() -> new RuntimeException("Assunto não encontrado"));
	}
}
