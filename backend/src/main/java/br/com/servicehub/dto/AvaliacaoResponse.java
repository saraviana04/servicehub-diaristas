package br.com.servicehub.dto;

public class AvaliacaoResponse {
    private Long id;
    private ClienteResponse cliente;
    private DiaristaResponse diarista;
    private Integer nota;
    private String comentario;

    public AvaliacaoResponse(Long id,
                             ClienteResponse cliente,
                             DiaristaResponse diarista,
                             Integer nota,
                             String comentario) {
        this.id = id;
        this.cliente = cliente;
        this.diarista = diarista;
        this.nota = nota;
        this.comentario = comentario;
    }

    public Long getId() {
        return id;
    }

    public ClienteResponse getCliente() {
        return cliente;
    }

    public DiaristaResponse getDiarista() {
        return diarista;
    }

    public Integer getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario;
    }
}
