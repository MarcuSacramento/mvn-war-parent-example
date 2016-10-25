package br.gov.mj.apoio.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_reg_regiao", schema = "apoio")
public class Regiao extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "reg_id_regiao")
    @NotNull
    private Long id;

    @Column(name = "reg_no_regiao")
    @NotNull
    private String nomeRegiao;

    @Column(name = "reg_no_sigla_regiao")
    @NotNull
    private String siglaRegiao;

    public Regiao() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeRegiao() {
        return nomeRegiao;
    }

    public void setNomeRegiao(String nomeRegiao) {
        this.nomeRegiao = nomeRegiao;
    }

    public String getSiglaRegiao() {
        return siglaRegiao;
    }

    public void setSiglaRegiao(String siglaRegiao) {
        this.siglaRegiao = siglaRegiao;
    }

    @Transient
    public String getNomeSigla() {
        return (this.nomeRegiao + " - " + this.siglaRegiao).toUpperCase();
    }
}