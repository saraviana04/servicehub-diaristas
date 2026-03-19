package br.com.servicehub.controller;

import br.com.servicehub.domain.Cliente;
import br.com.servicehub.domain.Diarista;
import br.com.servicehub.domain.Role;
import br.com.servicehub.domain.Usuario;
import br.com.servicehub.dto.AuthRequest;
import br.com.servicehub.dto.AuthResponse;
import br.com.servicehub.dto.MeResponse;
import br.com.servicehub.dto.RegisterRequest;
import br.com.servicehub.repository.ClienteRepository;
import br.com.servicehub.repository.DiaristaRepository;
import br.com.servicehub.repository.UsuarioRepository;
import br.com.servicehub.security.JwtService;
import br.com.servicehub.service.AuthUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final DiaristaRepository diaristaRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthUtils authUtils;

    public AuthController(UsuarioRepository usuarioRepository,
                          ClienteRepository clienteRepository,
                          DiaristaRepository diaristaRepository,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          AuthUtils authUtils) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.diaristaRepository = diaristaRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.authUtils = authUtils;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setEmail(request.getEmail());
        usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        usuario.setRole(request.getRole());

        if (request.getRole() == Role.CLIENTE) {
            validarCadastroBasico(request);
            Cliente cliente = new Cliente();
            cliente.setNome(request.getNome());
            cliente.setEmail(request.getEmail());
            cliente.setTelefone(request.getTelefone());
            cliente.setBairro(request.getBairro());
            clienteRepository.save(cliente);
            usuario.setCliente(cliente);
        }

        if (request.getRole() == Role.DIARISTA) {
            validarCadastroBasico(request);
            Diarista diarista = new Diarista();
            diarista.setNome(request.getNome());
            diarista.setEmail(request.getEmail());
            diarista.setTelefone(request.getTelefone());
            diarista.setBairro(request.getBairro());
            diarista.setExperiencia(request.getExperiencia());
            diaristaRepository.save(diarista);
            usuario.setDiarista(diarista);
        }

        usuarioRepository.save(usuario);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));

        String token = jwtService.generateToken((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal());
        return new AuthResponse(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));

        String token = jwtService.generateToken((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal());
        return new AuthResponse(token);
    }

    @GetMapping("/me")
    public MeResponse me() {
        Usuario usuario = authUtils.currentUsuario();
        Long clienteId = usuario.getCliente() != null ? usuario.getCliente().getId() : null;
        Long diaristaId = usuario.getDiarista() != null ? usuario.getDiarista().getId() : null;
        return new MeResponse(usuario.getEmail(), usuario.getRole(), clienteId, diaristaId);
    }

    private void validarCadastroBasico(RegisterRequest request) {
        if (request.getNome() == null || request.getNome().isBlank()
                || request.getTelefone() == null || request.getTelefone().isBlank()) {
            throw new IllegalArgumentException("Nome e telefone são obrigatórios para este perfil");
        }
    }
}
