package br.gov.mj.side.entidades.programa.inscricao;

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
@Table(name = "tb_ieb_inscricao_programa_local_entrega_bem", schema = "side")
public class InscricaoLocalEntregaBem extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ieb_id_inscricao_programa_local_entrega_bem")
    @SequenceGenerator(name = "tb_ieb_inscricao_programa_local_entrega_bem_generator", sequenceName = "side.seq_tb_ieb_inscricao_programa_local_entrega_bem", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_ieb_inscricao_programa_local_entrega_bem_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ieb_fk_ipb_id_inscricao_programa_bem")
    @NotNull
    private InscricaoProgramaBem inscricaoProgramaBem;

    @ManyToOne
    @JoinColumn(name = "ieb_fk_ile_id_inscricao_programa_local_entrega")
    @NotNull
    private InscricaoLocalEntrega inscricaoLocalEntrega;

    @Column(name = "ieb_qt_quantidade")
    @NotNull
    private Integer quantidade;

    public InscricaoLocalEntregaBem() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InscricaoProgramaBem getInscricaoProgramaBem() {
        return inscricaoProgramaBem;
    }

    public void setInscricaoProgramaBem(InscricaoProgramaBem inscricaoProgramaBem) {
        this.inscricaoProgramaBem = inscricaoProgramaBem;
    }

    public InscricaoLocalEntrega getInscricaoLocalEntrega() {
        return inscricaoLocalEntrega;
    }

    public void setInscricaoLocalEntrega(InscricaoLocalEntrega inscricaoLocalEntrega) {
        this.inscricaoLocalEntrega = inscricaoLocalEntrega;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

}