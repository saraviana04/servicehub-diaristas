package br.com.servicehub.dto;

public class AvaliacaoResponse {
    private Long id;
    private ClienteResponse cliente;
    private DiaristaResponse diarista;
    private Long agendamentoId;
    private Integer nota;
    private String comentario;

    public AvaliacaoResponse(Long id,
                             ClienteResponse cliente,
                             DiaristaResponse diarista,
                             Long agendamentoId,
                             Integer nota,
                             String comentario) {
        this.id = id;
        this.cliente = cliente;
        this.diarista = diarista;
        this.agendamentoId = agendamentoId;
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

    public Long getAgendamentoId() {
        return agendamentoId;
    }

    public Integer getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario;
    }
}
