package br.gov.mj.apoio.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_euf_estado_uf", schema = "apoio")
public class Uf extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "euf_id_estado")
    @NotNull
    private Long id;

    @Column(name = "euf_no_estado")
    @NotNull
    private String nomeUf;

    @Column(name = "euf_no_sigla_uf")
    @NotNull
    private String siglaUf;

    @ManyToOne
    @JoinColumn(name = "euf_fk_reg_id_regiao")
    @NotNull
    private Regiao regiao;

    public Uf() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeUf() {
        return nomeUf;
    }

    public void setNomeUf(String nomeUf) {
        this.nomeUf = nomeUf;
    }

    public String getSiglaUf() {
        return siglaUf;
    }

    public void setSiglaUf(String siglaUf) {
        this.siglaUf = siglaUf;
    }

    @Transient
    public String getNomeSigla() {
        return (this.nomeUf + " - " + this.siglaUf).toUpperCase();
    }

    public Regiao getRegiao() {
        return regiao;
    }

    public void setRegiao(Regiao regiao) {
        this.regiao = regiao;
    }
}