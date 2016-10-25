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
@Table(name = "tb_upe_usuario_perfil", schema = "seg")
public class UsuarioPerfil extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "upe_id_usuario_perfil")
    @SequenceGenerator(name = "tb_upe_usuario_perfil_generator", sequenceName = "seg.seq_tb_upe_usuario_perfil", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_upe_usuario_perfil_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "upe_fk_per_id_perfil")
    @NotNull
    private Perfil perfil;

    @ManyToOne
    @JoinColumn(name = "upe_fk_usu_id_usuario")
    @NotNull
    private Usuario usuario;

    public UsuarioPerfil() {
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}