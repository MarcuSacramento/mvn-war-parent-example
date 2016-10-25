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
@Table(name = "tb_uex_unidade_executora", schema = "apoio")
public class UnidadeExecutora extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "uex_id_unidade_executora")
    @NotNull
    private Long id;

    @Column(name = "uex_no_unidade_executora")
    @NotNull
    private String nomeUnidadeExecutora;

    @ManyToOne
    @JoinColumn(name = "uex_fk_org_id_orgao")
    @NotNull
    private Orgao orgao;

    public UnidadeExecutora() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeUnidadeExecutora() {
        return nomeUnidadeExecutora;
    }

    public void setNomeUnidadeExecutora(String nomeUnidadeExecutora) {
        this.nomeUnidadeExecutora = nomeUnidadeExecutora;
    }

    public Orgao getOrgao() {
        return orgao;
    }

    public void setOrgao(Orgao orgao) {
        this.orgao = orgao;
    }

    @Transient
    public String codigoNome() {
        return (this.id + " - " + this.nomeUnidadeExecutora).toUpperCase();
    }

}