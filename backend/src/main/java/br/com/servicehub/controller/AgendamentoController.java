package br.com.servicehub.controller;

import br.com.servicehub.domain.Agendamento;
import br.com.servicehub.domain.Cliente;
import br.com.servicehub.domain.Diarista;
import br.com.servicehub.dto.AgendamentoRequest;
import br.com.servicehub.dto.AgendamentoResponse;
import br.com.servicehub.domain.Role;
import br.com.servicehub.domain.Usuario;
import br.com.servicehub.repository.AgendamentoRepository;
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
@RequestMapping("/agendamentos")
public class AgendamentoController {
    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final DiaristaRepository diaristaRepository;
    private final AuthUtils authUtils;

    public AgendamentoController(AgendamentoRepository agendamentoRepository,
                                 ClienteRepository clienteRepository,
                                 DiaristaRepository diaristaRepository,
                                 AuthUtils authUtils) {
        this.agendamentoRepository = agendamentoRepository;
        this.clienteRepository = clienteRepository;
        this.diaristaRepository = diaristaRepository;
        this.authUtils = authUtils;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE','DIARISTA','ADMIN')")
    public Page<AgendamentoResponse> listar(Pageable pageable) {
        Usuario usuario = authUtils.currentUsuario();
        if (!authUtils.isAdmin(usuario)) {
            if (usuario.getRole() == Role.CLIENTE) {
                if (usuario.getCliente() == null) {
                    throw new IllegalArgumentException("Cliente não vinculado ao usuário");
                }
                return agendamentoRepository.findByClienteId(usuario.getCliente().getId(), pageable)
                        .map(ResponseMapper::toAgendamentoResponse);
            }
            if (usuario.getRole() == Role.DIARISTA) {
                if (usuario.getDiarista() == null) {
                    throw new IllegalArgumentException("Diarista não vinculada ao usuário");
                }
                return agendamentoRepository.findByDiaristaId(usuario.getDiarista().getId(), pageable)
                        .map(ResponseMapper::toAgendamentoResponse);
            }
        }
        return agendamentoRepository.findAll(pageable)
                .map(ResponseMapper::toAgendamentoResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','DIARISTA','ADMIN')")
    public AgendamentoResponse buscar(@PathVariable Long id) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));
        validarAcessoAgendamento(agendamento);
        return ResponseMapper.toAgendamentoResponse(agendamento);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CLIENTE','DIARISTA','ADMIN')")
    public AgendamentoResponse criar(@Valid @RequestBody AgendamentoRequest request) {
        validarAcessoRequest(request.getClienteId(), request.getDiaristaId());
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        Diarista diarista = diaristaRepository.findById(request.getDiaristaId())
                .orElseThrow(() -> new IllegalArgumentException("Diarista não encontrado"));

        Agendamento agendamento = new Agendamento();
        agendamento.setCliente(cliente);
        agendamento.setDiarista(diarista);
        agendamento.setDataServico(request.getDataServico());
        if (request.getStatus() != null) {
            agendamento.setStatus(request.getStatus());
        }
        agendamento.setObservacoes(request.getObservacoes());

        Agendamento salvo = agendamentoRepository.save(agendamento);
        return ResponseMapper.toAgendamentoResponse(salvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','DIARISTA','ADMIN')")
    public AgendamentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AgendamentoRequest request) {
        Agendamento existente = agendamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));
        validarAcessoAgendamento(existente);
        validarAcessoRequest(request.getClienteId(), request.getDiaristaId());
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        Diarista diarista = diaristaRepository.findById(request.getDiaristaId())
                .orElseThrow(() -> new IllegalArgumentException("Diarista não encontrado"));

        existente.setCliente(cliente);
        existente.setDiarista(diarista);
        existente.setDataServico(request.getDataServico());
        if (request.getStatus() != null) {
            existente.setStatus(request.getStatus());
        }
        existente.setObservacoes(request.getObservacoes());

        Agendamento salvo = agendamentoRepository.save(existente);
        return ResponseMapper.toAgendamentoResponse(salvo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('CLIENTE','DIARISTA','ADMIN')")
    public void remover(@PathVariable Long id) {
        Agendamento existente = agendamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado"));
        validarAcessoAgendamento(existente);
        agendamentoRepository.deleteById(id);
    }

    private void validarAcessoAgendamento(Agendamento agendamento) {
        Usuario usuario = authUtils.currentUsuario();
        if (authUtils.isAdmin(usuario)) {
            return;
        }
        if (usuario.getRole() == Role.CLIENTE) {
            if (usuario.getCliente() == null || !usuario.getCliente().getId().equals(agendamento.getCliente().getId())) {
                throw new IllegalArgumentException("Acesso negado ao agendamento");
            }
        }
        if (usuario.getRole() == Role.DIARISTA) {
            if (usuario.getDiarista() == null || !usuario.getDiarista().getId().equals(agendamento.getDiarista().getId())) {
                throw new IllegalArgumentException("Acesso negado ao agendamento");
            }
        }
    }

    private void validarAcessoRequest(Long clienteId, Long diaristaId) {
        Usuario usuario = authUtils.currentUsuario();
        if (authUtils.isAdmin(usuario)) {
            return;
        }
        if (usuario.getRole() == Role.CLIENTE) {
            if (usuario.getCliente() == null || !usuario.getCliente().getId().equals(clienteId)) {
                throw new IllegalArgumentException("Cliente não autorizado para este agendamento");
            }
        }
        if (usuario.getRole() == Role.DIARISTA) {
            if (usuario.getDiarista() == null || !usuario.getDiarista().getId().equals(diaristaId)) {
                throw new IllegalArgumentException("Diarista não autorizada para este agendamento");
            }
        }
    }
}
