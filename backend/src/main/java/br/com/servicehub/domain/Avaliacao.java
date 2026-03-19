package br.com.servicehub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "avaliacoes")
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    @JsonIgnoreProperties({"email", "telefone"})
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "diarista_id")
    @JsonIgnoreProperties({"email", "telefone", "experiencia"})
    private Diarista diarista;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer nota;

    private String comentario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Diarista getDiarista() {
        return diarista;
    }

    public void setDiarista(Diarista diarista) {
        this.diarista = diarista;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
