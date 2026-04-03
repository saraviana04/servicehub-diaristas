package br.com.servicehub.controller;

import br.com.servicehub.domain.Diarista;
import br.com.servicehub.domain.StatusAgendamento;
import br.com.servicehub.dto.DiaristaRequest;
import br.com.servicehub.dto.DiaristaPerfilResponse;
import br.com.servicehub.dto.DiaristaResponse;
import br.com.servicehub.domain.Usuario;
import br.com.servicehub.domain.Role;
import br.com.servicehub.dto.AvaliacaoResumoResponse;
import br.com.servicehub.repository.AgendamentoRepository;
import br.com.servicehub.repository.AvaliacaoRepository;
import br.com.servicehub.repository.DiaristaRepository;
import br.com.servicehub.service.AuthUtils;
import br.com.servicehub.service.ResponseMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/diaristas")
public class DiaristaController {
    private final DiaristaRepository repository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final AuthUtils authUtils;

    public DiaristaController(DiaristaRepository repository,
                              AvaliacaoRepository avaliacaoRepository,
                              AgendamentoRepository agendamentoRepository,
                              AuthUtils authUtils) {
        this.repository = repository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.agendamentoRepository = agendamentoRepository;
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
    public DiaristaPerfilResponse buscar(@PathVariable Long id) {
        Usuario usuario = authUtils.currentUsuario();
        if (!authUtils.isAdmin(usuario) && usuario.getRole() == Role.DIARISTA) {
            if (usuario.getDiarista() == null || !usuario.getDiarista().getId().equals(id)) {
                throw new IllegalArgumentException("Acesso negado à diarista");
            }
            return montarPerfil(usuario.getDiarista());
        }
        Diarista diarista = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Diarista não encontrada"));
        return montarPerfil(diarista);
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
        diarista.setBio(request.getBio());
        diarista.setEspecialidades(request.getEspecialidades());
        diarista.setDisponibilidade(request.getDisponibilidade());
        diarista.setMateriaisProprios(request.getMateriaisProprios());
        diarista.setAgendaFlexivel(request.getAgendaFlexivel());
        diarista.setChecklist(request.getChecklist());
        diarista.setRaioAtendimentoKm(request.getRaioAtendimentoKm());
        diarista.setPrecoBase(request.getPrecoBase());
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
        existente.setBio(request.getBio());
        existente.setEspecialidades(request.getEspecialidades());
        existente.setDisponibilidade(request.getDisponibilidade());
        existente.setMateriaisProprios(request.getMateriaisProprios());
        existente.setAgendaFlexivel(request.getAgendaFlexivel());
        existente.setChecklist(request.getChecklist());
        existente.setRaioAtendimentoKm(request.getRaioAtendimentoKm());
        existente.setPrecoBase(request.getPrecoBase());
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

    private DiaristaPerfilResponse montarPerfil(Diarista diarista) {
        Long diaristaId = diarista.getId();
        long totalAvaliacoes = avaliacaoRepository.countByDiaristaId(diaristaId);
        Double notaMedia = avaliacaoRepository.averageNotaByDiaristaId(diaristaId);
        long servicosConcluidos = agendamentoRepository.countByDiaristaIdAndStatus(diaristaId, StatusAgendamento.CONCLUIDO);
        java.util.List<AvaliacaoResumoResponse> avaliacoesRecentes = avaliacaoRepository
                .findByDiaristaId(diaristaId, PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "id")))
                .map(avaliacao -> new AvaliacaoResumoResponse(
                        avaliacao.getId(),
                        avaliacao.getNota(),
                        avaliacao.getComentario(),
                        avaliacao.getCliente() != null ? avaliacao.getCliente().getNome() : "Cliente"
                ))
                .getContent();

        java.util.List<String> especialidades = null;
        if (diarista.getEspecialidades() != null && !diarista.getEspecialidades().isBlank()) {
            especialidades = java.util.Arrays.stream(diarista.getEspecialidades().split(","))
                    .map(String::trim)
                    .filter(item -> !item.isBlank())
                    .collect(java.util.stream.Collectors.toList());
        }

        return new DiaristaPerfilResponse(
                diarista.getId(),
                diarista.getNome(),
                diarista.getBairro(),
                diarista.getExperiencia(),
                diarista.getBio(),
                especialidades,
                diarista.getDisponibilidade(),
                diarista.getMateriaisProprios(),
                diarista.getAgendaFlexivel(),
                diarista.getChecklist(),
                diarista.getRaioAtendimentoKm(),
                diarista.getPrecoBase(),
                notaMedia,
                totalAvaliacoes,
                servicosConcluidos,
                avaliacoesRecentes
        );
    }
}
