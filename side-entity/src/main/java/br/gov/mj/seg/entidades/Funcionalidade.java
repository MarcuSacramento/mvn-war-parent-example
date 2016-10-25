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
@Table(name = "tb_fnc_funcionalidade", schema = "seg")
public class Funcionalidade extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "fnc_id_funcionalidade")
    @SequenceGenerator(name = "tb_fnc_funcionalidade_generator", sequenceName = "seg.seq_tb_fnc_funcionalidade", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_fnc_funcionalidade_generator")
    @NotNull
    private Long id;

    @Column(name = "fnc_ds_descricao_funcionalidade")
    @NotNull
    private String descricaoFuncionalidade;

    @Column(name = "fnc_to_token_funcionalidade")
    @NotNull
    private String tokenFuncionalidade;

    @ManyToOne
    @JoinColumn(name = "fup_fk_sis_id_sistema")
    @NotNull
    private Sistema sistema;

    public Funcionalidade() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricaoFuncionalidade() {
        return descricaoFuncionalidade;
    }

    public void setDescricaoFuncionalidade(String descricaoFuncionalidade) {
        this.descricaoFuncionalidade = descricaoFuncionalidade;
    }

    public String getTokenFuncionalidade() {
        return tokenFuncionalidade;
    }

    public void setTokenFuncionalidade(String tokenFuncionalidade) {
        this.tokenFuncionalidade = tokenFuncionalidade;
    }

    public Sistema getSistema() {
        return sistema;
    }

    public void setSistema(Sistema sistema) {
        this.sistema = sistema;
    }

}