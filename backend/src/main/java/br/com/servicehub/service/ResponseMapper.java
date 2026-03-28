package br.com.servicehub.service;

import br.com.servicehub.domain.Agendamento;
import br.com.servicehub.domain.Avaliacao;
import br.com.servicehub.domain.Cliente;
import br.com.servicehub.domain.Diarista;
import br.com.servicehub.dto.AgendamentoResponse;
import br.com.servicehub.dto.AvaliacaoResponse;
import br.com.servicehub.dto.ClienteResponse;
import br.com.servicehub.dto.DiaristaResponse;

public final class ResponseMapper {
    private ResponseMapper() {
    }

    public static ClienteResponse toClienteResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getBairro());
    }

    public static DiaristaResponse toDiaristaResponse(Diarista diarista) {
        return new DiaristaResponse(diarista.getId(), diarista.getNome(), diarista.getBairro(), diarista.getExperiencia());
    }

    public static AgendamentoResponse toAgendamentoResponse(Agendamento agendamento) {
        return new AgendamentoResponse(
                agendamento.getId(),
                toClienteResponse(agendamento.getCliente()),
                toDiaristaResponse(agendamento.getDiarista()),
                agendamento.getDataServico(),
                agendamento.getStatus(),
                agendamento.getObservacoes(),
                agendamento.getCreatedAt()
        );
    }

    public static AvaliacaoResponse toAvaliacaoResponse(Avaliacao avaliacao) {
        return new AvaliacaoResponse(
                avaliacao.getId(),
                toClienteResponse(avaliacao.getCliente()),
                toDiaristaResponse(avaliacao.getDiarista()),
                avaliacao.getNota(),
                avaliacao.getComentario()
        );
    }
}
