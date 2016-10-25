package br.gov.mj.seg.entidades;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;
import br.gov.mj.seg.entidades.enums.EnumTipoUsuario;

@Entity
@Table(name = "tb_per_perfil", schema = "seg")
public class Perfil extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "per_id_perfil")
    @SequenceGenerator(name = "tb_per_perfil_generator", sequenceName = "seg.seq_tb_per_perfil", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_per_perfil_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "per_fk_sis_id_sistema")
    @NotNull
    private Sistema sistema;

    @Column(name = "per_ds_descricao_perfil")
    @NotNull
    private String descricaoPerfil;

    @Column(name = "per_no_nome_perfil")
    @NotNull
    private String nomePerfil;

    @Enumerated(EnumType.STRING)
    @Type(type = "br.gov.mj.infra.negocio.persistencia.BaseEnumType", parameters = @Parameter(value = "br.gov.mj.seg.entidades.enums.EnumTipoUsuario", name = "enumClassName"))
    @Column(name = "per_tp_tipo_usuario_perfil")
    @NotNull
    private EnumTipoUsuario tipoUsuarioPerfil;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "perfil", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private Set<PerfilFuncionalidade> funcionalidades = new HashSet<PerfilFuncionalidade>();

    public Perfil() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricaoPerfil() {
        return descricaoPerfil;
    }

    public void setDescricaoPerfil(String descricaoPerfil) {
        this.descricaoPerfil = descricaoPerfil;
    }

    public String getNomePerfil() {
        return nomePerfil;
    }

    public void setNomePerfil(String nomePerfil) {
        this.nomePerfil = nomePerfil;
    }

    public Sistema getSistema() {
        return sistema;
    }

    public void setSistema(Sistema sistema) {
        this.sistema = sistema;
    }

    public EnumTipoUsuario getTipoUsuarioPerfil() {
        return tipoUsuarioPerfil;
    }

    public void setTipoUsuarioPerfil(EnumTipoUsuario tipoUsuarioPerfil) {
        this.tipoUsuarioPerfil = tipoUsuarioPerfil;
    }

    public Set<PerfilFuncionalidade> getFuncionalidades() {
        return funcionalidades;
    }

    public void setFuncionalidades(Set<PerfilFuncionalidade> funcionalidades) {
        this.funcionalidades = funcionalidades;
    }

}