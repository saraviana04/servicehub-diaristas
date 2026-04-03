package br.com.servicehub.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AvaliacaoRequest {
    @NotNull(message = "clienteId é obrigatório")
    private Long clienteId;

    @NotNull(message = "diaristaId é obrigatório")
    private Long diaristaId;

    @NotNull(message = "agendamentoId é obrigatório")
    private Long agendamentoId;

    @NotNull(message = "nota é obrigatória")
    @Min(1)
    @Max(5)
    private Integer nota;

    private String comentario;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getDiaristaId() {
        return diaristaId;
    }

    public void setDiaristaId(Long diaristaId) {
        this.diaristaId = diaristaId;
    }

    public Long getAgendamentoId() {
        return agendamentoId;
    }

    public void setAgendamentoId(Long agendamentoId) {
        this.agendamentoId = agendamentoId;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
