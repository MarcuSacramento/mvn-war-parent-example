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
@Table(name = "tb_suf_subfuncao", schema = "apoio")
public class SubFuncao extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "suf_id_subfuncao")
    @NotNull
    private Long id;

    @Column(name = "suf_no_subfuncao")
    @NotNull
    private String nomeSubFuncao;

    @ManyToOne
    @JoinColumn(name = "suf_fk_fun_id_funcao")
    @NotNull
    private Funcao funcao;

    public SubFuncao() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeSubFuncao() {
        return nomeSubFuncao;
    }

    public void setNomeSubFuncao(String nomeSubFuncao) {
        this.nomeSubFuncao = nomeSubFuncao;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    @Transient
    public String getCodigoNome() {
        return (id + " - " + nomeSubFuncao).toUpperCase();
    }

}