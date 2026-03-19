package br.com.servicehub.controller;

import br.com.servicehub.domain.Diarista;
import br.com.servicehub.dto.DiaristaRequest;
import br.com.servicehub.dto.DiaristaResponse;
import br.com.servicehub.domain.Usuario;
import br.com.servicehub.domain.Role;
import br.com.servicehub.repository.DiaristaRepository;
import br.com.servicehub.service.AuthUtils;
import br.com.servicehub.service.ResponseMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/diaristas")
public class DiaristaController {
    private final DiaristaRepository repository;
    private final AuthUtils authUtils;

    public DiaristaController(DiaristaRepository repository, AuthUtils authUtils) {
        this.repository = repository;
        this.authUtils = authUtils;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE','DIARISTA','ADMIN')")
    public Page<DiaristaResponse> listar(@RequestParam(required = false) String bairro, Pageable pageable) {
        Usuario usuario = authUtils.currentUsuario();
        if (!authUtils.isAdmin(usuario) && usuario.getRole() == Role.DIARISTA) {
            if (usuario.getDiarista() == null) {
                throw new IllegalArgumentException("Diarista não vinculada ao usuário");
            }
            return new PageImpl<>(
                java.util.List.of(ResponseMapper.toDiaristaResponse(usuario.getDiarista())),
                    pageable,
                    1
            );
        }
        if (bairro != null && !bairro.isBlank()) {
            return repository.findByBairroIgnoreCaseContaining(bairro, pageable)
                    .map(ResponseMapper::toDiaristaResponse);
        }
        return repository.findAll(pageable)
                .map(ResponseMapper::toDiaristaResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','DIARISTA','ADMIN')")
    public DiaristaResponse buscar(@PathVariable Long id) {
        Usuario usuario = authUtils.currentUsuario();
        if (!authUtils.isAdmin(usuario) && usuario.getRole() == Role.DIARISTA) {
            if (usuario.getDiarista() == null || !usuario.getDiarista().getId().equals(id)) {
                throw new IllegalArgumentException("Acesso negado à diarista");
            }
            return ResponseMapper.toDiaristaResponse(usuario.getDiarista());
        }
        Diarista diarista = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Diarista não encontrada"));
        return ResponseMapper.toDiaristaResponse(diarista);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('DIARISTA','ADMIN')")
    public DiaristaResponse criar(@Valid @RequestBody DiaristaRequest request) {
        Usuario usuario = authUtils.currentUsuario();
        if (!authUtils.isAdmin(usuario)) {
            throw new IllegalArgumentException("Somente ADMIN pode criar diaristas");
        }
        Diarista diarista = new Diarista();
        diarista.setNome(request.getNome());
        diarista.setEmail(request.getEmail());
        diarista.setTelefone(request.getTelefone());
        diarista.setBairro(request.getBairro());
        diarista.setExperiencia(request.getExperiencia());
        Diarista salvo = repository.save(diarista);
        return ResponseMapper.toDiaristaResponse(salvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIARISTA','ADMIN')")
    public DiaristaResponse atualizar(@PathVariable Long id, @Valid @RequestBody DiaristaRequest request) {
        Usuario usuario = authUtils.currentUsuario();
        if (!authUtils.isAdmin(usuario)) {
            if (usuario.getDiarista() == null || !usuario.getDiarista().getId().equals(id)) {
                throw new IllegalArgumentException("Acesso negado à diarista");
            }
        }
        Diarista existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Diarista não encontrada"));
        existente.setNome(request.getNome());
        existente.setEmail(request.getEmail());
        existente.setTelefone(request.getTelefone());
        existente.setBairro(request.getBairro());
        existente.setExperiencia(request.getExperiencia());
        Diarista salvo = repository.save(existente);
        return ResponseMapper.toDiaristaResponse(salvo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('DIARISTA','ADMIN')")
    public void remover(@PathVariable Long id) {
        Usuario usuario = authUtils.currentUsuario();
        if (!authUtils.isAdmin(usuario)) {
            throw new IllegalArgumentException("Somente ADMIN pode remover diaristas");
        }
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Diarista não encontrada");
        }
        repository.deleteById(id);
    }
}
