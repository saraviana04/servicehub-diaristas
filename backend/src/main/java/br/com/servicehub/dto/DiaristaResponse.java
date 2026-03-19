package br.com.servicehub.dto;

public class DiaristaResponse {
    private Long id;
    private String nome;
    private String bairro;
    private String experiencia;

    public DiaristaResponse(Long id, String nome, String bairro, String experiencia) {
        this.id = id;
        this.nome = nome;
        this.bairro = bairro;
        this.experiencia = experiencia;
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
}
