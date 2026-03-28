package br.com.servicehub.dto;

import br.com.servicehub.domain.StatusAgendamento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AgendamentoResponse {
    private Long id;
    private ClienteResponse cliente;
    private DiaristaResponse diarista;
    private LocalDate dataServico;
    private StatusAgendamento status;
    private String observacoes;
    private LocalDateTime createdAt;

    public AgendamentoResponse(Long id,
                               ClienteResponse cliente,
                               DiaristaResponse diarista,
                               LocalDate dataServico,
                               StatusAgendamento status,
                               String observacoes,
                               LocalDateTime createdAt) {
        this.id = id;
        this.cliente = cliente;
        this.diarista = diarista;
        this.dataServico = dataServico;
        this.status = status;
        this.observacoes = observacoes;
        this.createdAt = createdAt;
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

    public LocalDate getDataServico() {
        return dataServico;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
