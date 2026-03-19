package br.com.servicehub.dto;

import br.com.servicehub.domain.StatusAgendamento;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AgendamentoRequest {
    @NotNull(message = "clienteId é obrigatório")
    private Long clienteId;

    @NotNull(message = "diaristaId é obrigatório")
    private Long diaristaId;

    @NotNull(message = "dataServico é obrigatória")
    @FutureOrPresent
    private LocalDate dataServico;

    private StatusAgendamento status;

    private String observacoes;

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

    public LocalDate getDataServico() {
        return dataServico;
    }

    public void setDataServico(LocalDate dataServico) {
        this.dataServico = dataServico;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
