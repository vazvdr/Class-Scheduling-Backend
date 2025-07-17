package com.class_project.backend_class.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.class_project.backend_class.classes.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	Optional<Usuario> findByEmail(String email);

}
