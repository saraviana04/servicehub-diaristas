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

    private String bio;

    private String especialidades;

    private String disponibilidade;

    private Boolean materiaisProprios;

    private Boolean agendaFlexivel;

    private Boolean checklist;

    private Integer raioAtendimentoKm;

    private java.math.BigDecimal precoBase;

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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(String especialidades) {
        this.especialidades = especialidades;
    }

    public String getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(String disponibilidade) {
        this.disponibilidade = disponibilidade;
    }

    public Boolean getMateriaisProprios() {
        return materiaisProprios;
    }

    public void setMateriaisProprios(Boolean materiaisProprios) {
        this.materiaisProprios = materiaisProprios;
    }

    public Boolean getAgendaFlexivel() {
        return agendaFlexivel;
    }

    public void setAgendaFlexivel(Boolean agendaFlexivel) {
        this.agendaFlexivel = agendaFlexivel;
    }

    public Boolean getChecklist() {
        return checklist;
    }

    public void setChecklist(Boolean checklist) {
        this.checklist = checklist;
    }

    public Integer getRaioAtendimentoKm() {
        return raioAtendimentoKm;
    }

    public void setRaioAtendimentoKm(Integer raioAtendimentoKm) {
        this.raioAtendimentoKm = raioAtendimentoKm;
    }

    public java.math.BigDecimal getPrecoBase() {
        return precoBase;
    }

    public void setPrecoBase(java.math.BigDecimal precoBase) {
        this.precoBase = precoBase;
    }
}
