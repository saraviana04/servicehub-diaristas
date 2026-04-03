package br.com.servicehub.controller;

import br.com.servicehub.domain.Agendamento;
import br.com.servicehub.domain.Avaliacao;
import br.com.servicehub.domain.Cliente;
import br.com.servicehub.domain.StatusAgendamento;
import br.com.servicehub.domain.Diarista;
import br.com.servicehub.domain.Role;
import br.com.servicehub.domain.Usuario;
import br.com.servicehub.dto.AvaliacaoRequest;
import br.com.servicehub.dto.AvaliacaoResponse;
import br.com.servicehub.repository.AgendamentoRepository;
import br.com.servicehub.repository.AvaliacaoRepository;
import br.com.servicehub.repository.ClienteRepository;
import br.com.servicehub.repository.DiaristaRepository;
import br.com.servicehub.service.AuthUtils;
import br.com.servicehub.service.ResponseMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {
    private final AvaliacaoRepository avaliacaoRepository;
    private final ClienteRepository clienteRepository;
    private final DiaristaRepository diaristaRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final AuthUtils authUtils;

    public AvaliacaoController(AvaliacaoRepository avaliacaoRepository,
                               ClienteRepository clienteRepository,
                               DiaristaRepository diaristaRepository,
                               AgendamentoRepository agendamentoRepository,
                               AuthUtils authUtils) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.clienteRepository = clienteRepository;
        this.diaristaRepository = diaristaRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.authUtils = authUtils;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE','DIARISTA','ADMIN')")
    public Page<AvaliacaoResponse> listar(Pageable pageable) {
        Usuario usuario = authUtils.currentUsuario();
        if (!authUtils.isAdmin(usuario)) {
            if (usuario.getRole() == Role.CLIENTE) {
                if (usuario.getCliente() == null) {
                    throw new IllegalArgumentException("Cliente não vinculado ao usuário");
                }
                return avaliacaoRepository.findByClienteId(usuario.getCliente().getId(), pageable)
                        .map(ResponseMapper::toAvaliacaoResponse);
            }
            if (usuario.getRole() == Role.DIARISTA) {
                if (usuario.getDiarista() == null) {
                    throw new IllegalArgumentException("Diarista não vinculada ao usuário");
                }
                return avaliacaoRepository.findByDiaristaId(usuario.getDiarista().getId(), pageable)
                        .map(ResponseMapper::toAvaliacaoResponse);
            }
        }
        return avaliacaoRepository.findAll(pageable)
                .map(ResponseMapper::toAvaliacaoResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','DIARISTA','ADMIN')")
    public AvaliacaoResponse buscar(@PathVariable Long id) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        validarAcessoAvaliacao(avaliacao);
        return ResponseMapper.toAvaliacaoResponse(avaliacao);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public AvaliacaoResponse criar(@Valid @RequestBody AvaliacaoRequest request) {
        validarAcessoRequest(request.getClienteId());
        if (avaliacaoRepository.existsByAgendamentoId(request.getAgendamentoId())) {
            throw new IllegalArgumentException("Este agendamento já foi avaliado");
        }
        Agendamento agendamento = agendamentoRepository.findById(request.getAgendamentoId())
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));
        if (agendamento.getStatus() != StatusAgendamento.CONCLUIDO) {
            throw new IllegalArgumentException("O serviço ainda não foi concluído");
        }
        if (agendamento.getCliente() == null || !agendamento.getCliente().getId().equals(request.getClienteId())) {
            throw new IllegalArgumentException("Cliente não autorizado para esta avaliação");
        }
        if (agendamento.getDiarista() == null || !agendamento.getDiarista().getId().equals(request.getDiaristaId())) {
            throw new IllegalArgumentException("Diarista não corresponde ao agendamento");
        }
        Cliente cliente = agendamento.getCliente();
        Diarista diarista = agendamento.getDiarista();

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setCliente(cliente);
        avaliacao.setDiarista(diarista);
        avaliacao.setAgendamento(agendamento);
        avaliacao.setNota(request.getNota());
        avaliacao.setComentario(request.getComentario());

        Avaliacao salva = avaliacaoRepository.save(avaliacao);
        return ResponseMapper.toAvaliacaoResponse(salva);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public AvaliacaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AvaliacaoRequest request) {
        Avaliacao existente = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        validarAcessoAvaliacao(existente);
        validarAcessoRequest(request.getClienteId());
        Agendamento agendamento = agendamentoRepository.findById(request.getAgendamentoId())
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));
        if (existente.getAgendamento() != null
                && !existente.getAgendamento().getId().equals(request.getAgendamentoId())) {
            throw new IllegalArgumentException("Não é permitido alterar o agendamento da avaliação");
        }
        if (agendamento.getCliente() == null || !agendamento.getCliente().getId().equals(request.getClienteId())) {
            throw new IllegalArgumentException("Cliente não autorizado para esta avaliação");
        }
        if (agendamento.getDiarista() == null || !agendamento.getDiarista().getId().equals(request.getDiaristaId())) {
            throw new IllegalArgumentException("Diarista não corresponde ao agendamento");
        }
        Cliente cliente = agendamento.getCliente();
        Diarista diarista = agendamento.getDiarista();

        existente.setCliente(cliente);
        existente.setDiarista(diarista);
        existente.setAgendamento(agendamento);
        existente.setNota(request.getNota());
        existente.setComentario(request.getComentario());

        Avaliacao salva = avaliacaoRepository.save(existente);
        return ResponseMapper.toAvaliacaoResponse(salva);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public void remover(@PathVariable Long id) {
        Avaliacao existente = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));
        validarAcessoAvaliacao(existente);
        avaliacaoRepository.deleteById(id);
    }

    private void validarAcessoAvaliacao(Avaliacao avaliacao) {
        Usuario usuario = authUtils.currentUsuario();
        if (authUtils.isAdmin(usuario)) {
            return;
        }
        if (usuario.getRole() == Role.CLIENTE) {
            if (usuario.getCliente() == null || !usuario.getCliente().getId().equals(avaliacao.getCliente().getId())) {
                throw new IllegalArgumentException("Acesso negado à avaliação");
            }
        }
        if (usuario.getRole() == Role.DIARISTA) {
            if (usuario.getDiarista() == null || !usuario.getDiarista().getId().equals(avaliacao.getDiarista().getId())) {
                throw new IllegalArgumentException("Acesso negado à avaliação");
            }
        }
    }

    private void validarAcessoRequest(Long clienteId) {
        Usuario usuario = authUtils.currentUsuario();
        if (authUtils.isAdmin(usuario)) {
            return;
        }
        if (usuario.getRole() == Role.CLIENTE) {
            if (usuario.getCliente() == null || !usuario.getCliente().getId().equals(clienteId)) {
                throw new IllegalArgumentException("Cliente não autorizado para esta avaliação");
            }
        }
    }
}
