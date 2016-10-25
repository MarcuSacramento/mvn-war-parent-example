package br.gov.mj.seg.entidades;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.gov.mj.infra.negocio.persistencia.BaseEntity;

@Entity
@Table(name = "tb_pfu_perfil_funcionalidade", schema = "seg")
public class PerfilFuncionalidade extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "pfu_id_perfil_funcionalidade")
    @SequenceGenerator(name = "tb_pfu_perfil_funcionalidade_generator", sequenceName = "seg.seq_tb_pfu_perfil_funcionalidade", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_pfu_perfil_funcionalidade_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pfu_fk_per_id_perfil")
    @NotNull
    private Perfil perfil;

    @ManyToOne
    @JoinColumn(name = "pfu_fk_fnc_id_funcionalidade")
    @NotNull
    private Funcionalidade funcionalidade;

    public PerfilFuncionalidade() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public Funcionalidade getFuncionalidade() {
        return funcionalidade;
    }

    public void setFuncionalidade(Funcionalidade funcionalidade) {
        this.funcionalidade = funcionalidade;
    }

}