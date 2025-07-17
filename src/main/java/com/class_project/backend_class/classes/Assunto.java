package com.class_project.backend_class.classes;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
public class Assunto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private int duracao;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "assunto_professor",
        joinColumns = @JoinColumn(name = "assunto_id"),
        inverseJoinColumns = @JoinColumn(name = "professor_id")
    )
    @JsonManagedReference("assuntos")
    private List<Professor> professores;

    public Assunto() {}

    public Assunto(Long id, String nome, int duracao, List<Professor> professores) {
        this.id = id;
        this.nome = nome;
        this.duracao = duracao;
        this.professores = professores;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public List<Professor> getProfessores() {
        return professores;
    }

    public void setProfessores(List<Professor> professores) {
        this.professores = professores;
    }
}
