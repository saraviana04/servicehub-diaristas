package br.com.servicehub.repository;

import br.com.servicehub.domain.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Page<Avaliacao> findByClienteId(Long clienteId, Pageable pageable);
    Page<Avaliacao> findByDiaristaId(Long diaristaId, Pageable pageable);
}
