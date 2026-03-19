package br.com.servicehub.repository;

import br.com.servicehub.domain.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    Page<Agendamento> findByClienteId(Long clienteId, Pageable pageable);
    Page<Agendamento> findByDiaristaId(Long diaristaId, Pageable pageable);
}
