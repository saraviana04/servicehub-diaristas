package br.com.servicehub.controller;

import br.com.servicehub.domain.Cliente;
import br.com.servicehub.dto.ClienteRequest;
import br.com.servicehub.dto.ClienteResponse;
import br.com.servicehub.domain.Usuario;
import br.com.servicehub.repository.ClienteRepository;
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
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteRepository repository;
    private final AuthUtils authUtils;

    public ClienteController(ClienteRepository repository, AuthUtils authUtils) {
        this.repository = repository;
        this.authUtils = authUtils;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public Page<ClienteResponse> listar(@RequestParam(required = false) String bairro, Pageable pageable) {
        Usuario usuario = authUtils.currentUsuario();
        if (!authUtils.isAdmin(usuario)) {
            if (usuario.getCliente() == null) {
                throw new IllegalArgumentException("Cliente não vinculado ao usuário");
            }
            return new PageImpl<>(
                    java.util.List.of(ResponseMapper.toClienteResponse(usuario.getCliente())),
                    pageable,
                    1
            );
        }
        if (bairro != null && !bairro.isBlank()) {
            return repository.findByBairroIgnoreCaseContaining(bairro, pageable)
                    .map(ResponseMapper::toClienteResponse);
        }
        return repository.findAll(pageable)
                .map(ResponseMapper::toClienteResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ClienteResponse buscar(@PathVariable Long id) {
        Usuario usuario = authUtils.currentUsuario();
        if (!authUtils.isAdmin(usuario)) {
            if (usuario.getCliente() == null || !usuario.getCliente().getId().equals(id)) {
                throw new IllegalArgumentException("Acesso negado ao cliente");
            }
            return ResponseMapper.toClienteResponse(usuario.getCliente());
        }
        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        return ResponseMapper.toClienteResponse(cliente);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ClienteResponse criar(@Valid @RequestBody ClienteRequest request) {
        Usuario usuario = authUtils.currentUsuario();
        if (!authUtils.isAdmin(usuario)) {
            throw new IllegalArgumentException("Somente ADMIN pode criar clientes");
        }
        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setEmail(request.getEmail());
        cliente.setTelefone(request.getTelefone());
        cliente.setBairro(request.getBairro());
        Cliente salvo = repository.save(cliente);
        return ResponseMapper.toClienteResponse(salvo);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ClienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequest request) {
        Usuario usuario = authUtils.currentUsuario();
        if (!authUtils.isAdmin(usuario)) {
            if (usuario.getCliente() == null || !usuario.getCliente().getId().equals(id)) {
                throw new IllegalArgumentException("Acesso negado ao cliente");
            }
        }
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
        existente.setNome(request.getNome());
        existente.setEmail(request.getEmail());
        existente.setTelefone(request.getTelefone());
        existente.setBairro(request.getBairro());
        Cliente salvo = repository.save(existente);
        return ResponseMapper.toClienteResponse(salvo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public void remover(@PathVariable Long id) {
        Usuario usuario = authUtils.currentUsuario();
        if (!authUtils.isAdmin(usuario)) {
            throw new IllegalArgumentException("Somente ADMIN pode remover clientes");
        }
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Cliente não encontrado");
        }
        repository.deleteById(id);
    }
}
