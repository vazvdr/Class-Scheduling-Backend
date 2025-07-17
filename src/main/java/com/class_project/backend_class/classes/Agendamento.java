package com.class_project.backend_class.classes;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "agendamento")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "assunto_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Assunto assunto;

    @ManyToOne
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horario;    

    public Agendamento() {}

    public Agendamento(Long id, Usuario usuario, Assunto assunto, Professor professor, LocalDate data, LocalTime horario) {
        this.id = id;
        this.usuario = usuario;
        this.assunto = assunto;
        this.professor = professor;
        this.data = data;
        this.horario = horario;
    }

    public Long getId() { 
    	return id; 
    }
    public void setId(Long id) { 
    	this.id = id; 
    }

    public Usuario getUsuario() { 
    	return usuario; 
    }
    public void setUsuario(Usuario usuario) { 
    	this.usuario = usuario; 
    }

    public Assunto getAssunto() { 
    	return assunto; 
    }
    public void setAssunto(Assunto assunto) { 
    	this.assunto = assunto; 
    }

    public Professor getProfessor() { 
    	return professor; 
    }
    public void setProfessor(Professor professor) { 
    	this.professor = professor; 
    }

    public LocalDate getData() { 
    	return data; 
    }
    public void setData(LocalDate data) { 
    	this.data = data; 
    }

    public LocalTime getHorario() { 
    	return horario; 
    }
    public void setHorario(LocalTime horario) { 
    	this.horario = horario; 
    }

}
