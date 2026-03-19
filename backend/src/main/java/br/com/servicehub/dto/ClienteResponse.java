package br.com.servicehub.dto;

public class ClienteResponse {
    private Long id;
    private String nome;
    private String bairro;

    public ClienteResponse(Long id, String nome, String bairro) {
        this.id = id;
        this.nome = nome;
        this.bairro = bairro;
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
}
