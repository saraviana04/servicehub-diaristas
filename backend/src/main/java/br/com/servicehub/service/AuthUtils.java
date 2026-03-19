package br.com.servicehub.service;

import br.com.servicehub.domain.Role;
import br.com.servicehub.domain.Usuario;
import br.com.servicehub.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {
    private final UsuarioRepository usuarioRepository;

    public AuthUtils(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario currentUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalArgumentException("Usuário não autenticado");
        }
        return usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public boolean isAdmin(Usuario usuario) {
        return usuario.getRole() == Role.ADMIN;
    }
}
