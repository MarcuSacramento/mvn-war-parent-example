package br.gov.mj.apoio.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_ppo_partido_politico", schema = "apoio")
public class PartidoPolitico extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ppo_id_partido_politico")
    @NotNull
    private Long id;

    @Column(name = "ppo_no_mome_partido")
    @NotNull
    private String nomePartido;

    @Column(name = "ppo_no_sigla_partido")
    @NotNull
    private String siglaPartido;

    public PartidoPolitico() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomePartido() {
        return nomePartido;
    }

    public void setNomePartido(String nomePartido) {
        this.nomePartido = nomePartido;
    }

    public String getSiglaPartido() {
        return siglaPartido;
    }

    public void setSiglaPartido(String siglaPartido) {
        this.siglaPartido = siglaPartido;
    }

    @Transient
    public String getSiglaNome() {
        return (this.siglaPartido + " - " + this.nomePartido).toUpperCase();
    }

}