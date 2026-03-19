package br.com.servicehub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class DiaristaRequest {
    @NotBlank(message = "nome é obrigatório")
    private String nome;

    @Email(message = "email inválido")
    @NotBlank(message = "email é obrigatório")
    private String email;

    @NotBlank(message = "telefone é obrigatório")
    private String telefone;

    private String bairro;

    private String experiencia;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(String experiencia) {
        this.experiencia = experiencia;
    }
}
