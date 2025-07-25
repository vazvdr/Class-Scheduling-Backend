package com.class_project.backend_class.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsuarioException extends RuntimeException {
	private static final long serialVersionUID = 1L;
    public UsuarioException(String mensagem) {
        super(mensagem);
    }
}
