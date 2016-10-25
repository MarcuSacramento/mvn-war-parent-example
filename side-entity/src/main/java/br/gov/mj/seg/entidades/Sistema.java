package br.gov.mj.seg.entidades;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_sis_sistema", schema = "seg")
public class Sistema extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "sis_id_sistema")
    @NotNull
    private Long id;

    @Column(name = "sis_ds_descricao_sistema")
    @NotNull
    private String descricaoSistema;

    @Column(name = "sis_no_nome_sistema")
    @NotNull
    private String nomeSistema;

    @Column(name = "sis_sg_sigla_sistema")
    @NotNull
    private String siglaSistema;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "sistema", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private Set<Perfil> perfis = new HashSet<Perfil>();

    public Sistema() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricaoSistema() {
        return descricaoSistema;
    }

    public void setDescricaoSistema(String descricaoSistema) {
        this.descricaoSistema = descricaoSistema;
    }

    public String getNomeSistema() {
        return nomeSistema;
    }

    public void setNomeSistema(String nomeSistema) {
        this.nomeSistema = nomeSistema;
    }

    public String getSiglaSistema() {
        return siglaSistema;
    }

    public void setSiglaSistema(String siglaSistema) {
        this.siglaSistema = siglaSistema;
    }

    public Set<Perfil> getPerfis() {
        return perfis;
    }

    public void setPerfis(Set<Perfil> perfis) {
        this.perfis = perfis;
    }

}