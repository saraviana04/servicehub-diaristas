package br.com.servicehub.repository;

import br.com.servicehub.domain.Diarista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DiaristaRepository extends JpaRepository<Diarista, Long> {
    Page<Diarista> findByBairroIgnoreCaseContaining(String bairro, Pageable pageable);
}
