package br.com.servicehub.repository;

import br.com.servicehub.domain.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Page<Avaliacao> findByClienteId(Long clienteId, Pageable pageable);
    Page<Avaliacao> findByDiaristaId(Long diaristaId, Pageable pageable);
    boolean existsByAgendamentoId(Long agendamentoId);

    long countByDiaristaId(Long diaristaId);

    @Query("select avg(a.nota) from Avaliacao a where a.diarista.id = :diaristaId")
    Double averageNotaByDiaristaId(@Param("diaristaId") Long diaristaId);
}
