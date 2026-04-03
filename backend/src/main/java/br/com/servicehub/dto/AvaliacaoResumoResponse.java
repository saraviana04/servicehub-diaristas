package br.com.servicehub.dto;

public class AvaliacaoResumoResponse {
    private Long id;
    private Integer nota;
    private String comentario;
    private String clienteNome;

    public AvaliacaoResumoResponse(Long id, Integer nota, String comentario, String clienteNome) {
        this.id = id;
        this.nota = nota;
        this.comentario = comentario;
        this.clienteNome = clienteNome;
    }

    public Long getId() {
        return id;
    }

    public Integer getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario;
    }

    public String getClienteNome() {
        return clienteNome;
    }
}
