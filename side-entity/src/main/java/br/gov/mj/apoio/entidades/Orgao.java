package br.gov.mj.apoio.entidades;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_org_orgao", schema = "apoio")
public class Orgao extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "org_id_orgao")
    @NotNull
    private Long id;

    @Column(name = "org_no_orgao")
    @NotNull
    private String nomeOrgao;

    @Column(name = "org_no_sigla_orgao")
    @NotNull
    private String siglaOrgao;

    @OneToMany(mappedBy = "orgao")
    private List<UnidadeExecutora> unidadesExecutoras = new ArrayList<UnidadeExecutora>();

    public Orgao() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeOrgao() {
        return nomeOrgao;
    }

    public void setNomeOrgao(String nomeOrgao) {
        this.nomeOrgao = nomeOrgao;
    }

    public List<UnidadeExecutora> getUnidadesExecutoras() {
        return unidadesExecutoras;
    }

    public void setUnidadesExecutoras(List<UnidadeExecutora> unidadesExecutoras) {
        this.unidadesExecutoras = unidadesExecutoras;
    }

    public String getSiglaOrgao() {
        return siglaOrgao;
    }

    public void setSiglaOrgao(String siglaOrgao) {
        this.siglaOrgao = siglaOrgao;
    }

    @Transient
    public String getCodigoOrgaoSigla() {
        return (this.id + " - " + this.nomeOrgao + " - " + this.siglaOrgao).toUpperCase();
    }

}