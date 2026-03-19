package br.com.servicehub.dto;

import br.com.servicehub.domain.Role;

public class MeResponse {
    private String email;
    private Role role;
    private Long clienteId;
    private Long diaristaId;

    public MeResponse(String email, Role role, Long clienteId, Long diaristaId) {
        this.email = email;
        this.role = role;
        this.clienteId = clienteId;
        this.diaristaId = diaristaId;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public Long getDiaristaId() {
        return diaristaId;
    }
}
