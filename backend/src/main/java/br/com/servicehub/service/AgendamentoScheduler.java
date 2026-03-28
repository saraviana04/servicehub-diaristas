package br.com.servicehub.service;

import br.com.servicehub.domain.Agendamento;
import br.com.servicehub.domain.StatusAgendamento;
import br.com.servicehub.repository.AgendamentoRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AgendamentoScheduler {
    private static final long PRAZO_HORAS = 48;
    private final AgendamentoRepository agendamentoRepository;

    public AgendamentoScheduler(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }

    @Scheduled(fixedDelay = 60 * 60 * 1000)
    @Transactional
    public void cancelarPendentesExpirados() {
        LocalDateTime limite = LocalDateTime.now().minusHours(PRAZO_HORAS);
        List<Agendamento> expirados = agendamentoRepository
                .findByStatusAndCreatedAtBefore(StatusAgendamento.PENDENTE, limite);
        if (expirados.isEmpty()) {
            return;
        }
        expirados.forEach(agendamento -> agendamento.setStatus(StatusAgendamento.CANCELADO));
        agendamentoRepository.saveAll(expirados);
    }
}
