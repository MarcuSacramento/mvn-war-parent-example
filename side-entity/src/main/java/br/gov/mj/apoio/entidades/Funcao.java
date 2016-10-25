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
@Table(name = "tb_fun_funcao", schema = "apoio")
public class Funcao extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "fun_id_funcao")
    @NotNull
    private Long id;

    @Column(name = "fun_no_funcao")
    @NotNull
    private String nomeFuncao;

    @OneToMany(mappedBy = "funcao")
    private List<SubFuncao> subFuncoes = new ArrayList<SubFuncao>();

    public Funcao() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeFuncao() {
        return nomeFuncao;
    }

    public void setNomeFuncao(String nomeFuncao) {
        this.nomeFuncao = nomeFuncao;
    }

    public List<SubFuncao> getSubFuncoes() {
        return subFuncoes;
    }

    public void setSubFuncoes(List<SubFuncao> subFuncoes) {
        this.subFuncoes = subFuncoes;
    }

    @Transient
    public String getCodigoNome() {
        return (this.id + " - " + this.nomeFuncao).toUpperCase();
    }

}