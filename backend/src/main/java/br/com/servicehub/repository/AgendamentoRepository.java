package br.com.servicehub.repository;

import br.com.servicehub.domain.Agendamento;
import br.com.servicehub.domain.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    Page<Agendamento> findByClienteId(Long clienteId, Pageable pageable);
    Page<Agendamento> findByDiaristaId(Long diaristaId, Pageable pageable);
    List<Agendamento> findByStatusAndCreatedAtBefore(StatusAgendamento status, LocalDateTime limite);
    long countByDiaristaIdAndStatus(Long diaristaId, StatusAgendamento status);
}
