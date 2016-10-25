package br.gov.mj.side.entidades;

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
@Table(name = "tb_tbm_tag_bem", schema = "side")
public class TagBem extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "tbm_id_tag_bem")
    @SequenceGenerator(name = "TB_TBM_TAG_BEM_TBMIDTAGBEM_GENERATOR", sequenceName = "SIDE.SEQ_TB_TBM_TAG_BEM", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TB_TBM_TAG_BEM_TBMIDTAGBEM_GENERATOR")
    @NotNull
    private Long id;

    @Column(name = "tbm_no_nome_tag")
    @NotNull
    private String nomeTag;

    @Column(name = "tbm_vl_valor_tag")
    @NotNull
    private String valorTag;

    @ManyToOne
    @JoinColumn(name = "tbm_fk_bem_id_bem")
    @NotNull
    private Bem bem;

    public TagBem() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeTag() {
        return nomeTag;
    }

    public void setNomeTag(String nomeTag) {
        this.nomeTag = nomeTag;
    }

    public String getValorTag() {
        return valorTag;
    }

    public void setValorTag(String valorTag) {
        this.valorTag = valorTag;
    }

    public Bem getBem() {
        return bem;
    }

    public void setBem(Bem bem) {
        this.bem = bem;
    }

}