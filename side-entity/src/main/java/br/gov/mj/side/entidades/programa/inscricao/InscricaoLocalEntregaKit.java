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
@Table(name = "tb_iek_inscricao_programa_local_entrega_kit", schema = "side")
public class InscricaoLocalEntregaKit extends BaseEntity<Long> {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "iek_id_inscricao_programa_local_entrega_kit")
    @SequenceGenerator(name = "tb_iek_inscricao_programa_local_entrega_kit_generator", sequenceName = "side.seq_tb_iek_inscricao_programa_local_entrega_kit", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_iek_inscricao_programa_local_entrega_kit_generator")
    @NotNull
    private Long id;

    @ManyToOne
    @JoinColumn(name = "iek_fk_ipk_id_inscricao_programa_kit")
    @NotNull
    private InscricaoProgramaKit inscricaoProgramaKit;

    @ManyToOne
    @JoinColumn(name = "iek_fk_ile_id_inscricao_programa_local_entrega")
    @NotNull
    private InscricaoLocalEntrega inscricaoLocalEntrega;

    @Column(name = "iek_qt_quantidade")
    @NotNull
    private Integer quantidade;

    public InscricaoLocalEntregaKit() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InscricaoProgramaKit getInscricaoProgramaKit() {
        return inscricaoProgramaKit;
    }

    public void setInscricaoProgramaKit(InscricaoProgramaKit inscricaoProgramaKit) {
        this.inscricaoProgramaKit = inscricaoProgramaKit;
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