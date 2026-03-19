package br.com.servicehub.repository;

import br.com.servicehub.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Page<Cliente> findByBairroIgnoreCaseContaining(String bairro, Pageable pageable);
}
