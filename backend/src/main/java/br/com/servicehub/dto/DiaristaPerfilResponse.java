package br.com.servicehub.dto;

import java.math.BigDecimal;
import java.util.List;

public class DiaristaPerfilResponse {
    private Long id;
    private String nome;
    private String bairro;
    private String experiencia;
    private String bio;
    private List<String> especialidades;
    private String disponibilidade;
    private Boolean materiaisProprios;
    private Boolean agendaFlexivel;
    private Boolean checklist;
    private Integer raioAtendimentoKm;
    private BigDecimal precoBase;
    private Double notaMedia;
    private Long totalAvaliacoes;
    private Long servicosConcluidos;
    private List<AvaliacaoResumoResponse> avaliacoesRecentes;

    public DiaristaPerfilResponse(Long id,
                                  String nome,
                                  String bairro,
                                  String experiencia,
                                  String bio,
                                  List<String> especialidades,
                                  String disponibilidade,
                                  Boolean materiaisProprios,
                                  Boolean agendaFlexivel,
                                  Boolean checklist,
                                  Integer raioAtendimentoKm,
                                  BigDecimal precoBase,
                                  Double notaMedia,
                                  Long totalAvaliacoes,
                                  Long servicosConcluidos,
                                  List<AvaliacaoResumoResponse> avaliacoesRecentes) {
        this.id = id;
        this.nome = nome;
        this.bairro = bairro;
        this.experiencia = experiencia;
        this.bio = bio;
        this.especialidades = especialidades;
        this.disponibilidade = disponibilidade;
        this.materiaisProprios = materiaisProprios;
        this.agendaFlexivel = agendaFlexivel;
        this.checklist = checklist;
        this.raioAtendimentoKm = raioAtendimentoKm;
        this.precoBase = precoBase;
        this.notaMedia = notaMedia;
        this.totalAvaliacoes = totalAvaliacoes;
        this.servicosConcluidos = servicosConcluidos;
        this.avaliacoesRecentes = avaliacoesRecentes;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getBairro() {
        return bairro;
    }

    public String getExperiencia() {
        return experiencia;
    }

    public String getBio() {
        return bio;
    }

    public List<String> getEspecialidades() {
        return especialidades;
    }

    public String getDisponibilidade() {
        return disponibilidade;
    }

    public Boolean getMateriaisProprios() {
        return materiaisProprios;
    }

    public Boolean getAgendaFlexivel() {
        return agendaFlexivel;
    }

    public Boolean getChecklist() {
        return checklist;
    }

    public Integer getRaioAtendimentoKm() {
        return raioAtendimentoKm;
    }

    public BigDecimal getPrecoBase() {
        return precoBase;
    }

    public Double getNotaMedia() {
        return notaMedia;
    }

    public Long getTotalAvaliacoes() {
        return totalAvaliacoes;
    }

    public Long getServicosConcluidos() {
        return servicosConcluidos;
    }

    public List<AvaliacaoResumoResponse> getAvaliacoesRecentes() {
        return avaliacoesRecentes;
    }
}
